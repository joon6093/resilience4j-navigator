package io.jeyong.resilience4j.navigator;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationToMethodResolver {

    private static final String CIRCUIT_BREAKER_FQN = "io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker";

    public @Nullable PsiElement[] resolveFromLiteral(PsiLiteralExpression literal) {
        var pair = PsiTreeUtil.getParentOfType(literal, PsiNameValuePair.class);
        if (pair == null) {
            return null;
        }
        if (!"fallbackMethod".equals(pair.getName())) {
            return null;
        }

        var annotation = getCircuitBreakerAnnotation(pair);
        if (annotation == null) {
            return null;
        }

        var methodName = stringValue(literal);
        return findMethodsInSameClass(annotation, methodName);
    }

    private @Nullable PsiAnnotation getCircuitBreakerAnnotation(PsiElement element) {
        var annotation = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
        if (annotation == null) {
            return null;
        }
        if (!CIRCUIT_BREAKER_FQN.equals(annotation.getQualifiedName())) {
            return null;
        }
        return annotation;
    }

    private @Nullable PsiElement[] findMethodsInSameClass(
            PsiAnnotation annotation,
            @Nullable String methodName
    ) {
        if (methodName == null) {
            return null;
        }

        var annotatedMethod = PsiTreeUtil.getParentOfType(annotation, PsiMethod.class);
        if (annotatedMethod == null) {
            return null;
        }

        var owner = annotatedMethod.getContainingClass();
        if (owner == null) {
            return null;
        }

        List<PsiElement> hits = new ArrayList<>();
        for (var method : owner.getMethods()) {
            if (isValidFallback(method, annotatedMethod, methodName)) {
                hits.add(method);
            }
        }

        if (hits.isEmpty()) {
            return null;
        }
        return hits.toArray(PsiElement[]::new);
    }

    private boolean isValidFallback(PsiMethod candidate, PsiMethod original, String methodName) {
        if (!Objects.equals(candidate.getName(), methodName)) {
            return false;
        }

        PsiParameter[] origParams = original.getParameterList()
                .getParameters();
        PsiParameter[] candParams = candidate.getParameterList()
                .getParameters();

        if (candParams.length == origParams.length) {
            for (int i = 0; i < origParams.length; i++) {
                if (!candParams[i].getType()
                        .equals(origParams[i].getType())) {
                    return false;
                }
            }
            return true;
        }

        if (candParams.length == origParams.length + 1
                && "java.lang.Throwable".equals(candParams[candParams.length - 1].getType()
                .getCanonicalText())) {
            for (int i = 0; i < origParams.length; i++) {
                if (!candParams[i].getType()
                        .equals(origParams[i].getType())) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private @Nullable String stringValue(@Nullable PsiElement expr) {
        if (expr instanceof PsiLiteralExpression literal) {
            var value = literal.getValue();
            if (value instanceof String string) {
                return string;
            }
        }
        return null;
    }
}
