package io.jeyong.resilience4j.navigator;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class AnnotationToMethodResolver {

    private static final String CIRCUIT_BREAKER_FQN = "io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker";

    public @Nullable PsiElement[] resolveFromLiteral(PsiLiteralExpression literal) {
        PsiNameValuePair pair = PsiTreeUtil.getParentOfType(literal, PsiNameValuePair.class);
        if (pair == null) {
            return null;
        }
        if (!"fallbackMethod".equals(pair.getName())) {
            return null;
        }

        PsiAnnotation annotation = getCircuitBreakerAnnotation(pair);
        if (annotation == null) {
            return null;
        }

        String methodName = stringValue(literal);
        return findMethodsInSameClass(annotation, methodName);
    }

    private @Nullable PsiAnnotation getCircuitBreakerAnnotation(PsiElement element) {
        PsiAnnotation annotation = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
        if (annotation == null) {
            return null;
        }
        if (!CIRCUIT_BREAKER_FQN.equals(annotation.getQualifiedName())) {
            return null;
        }
        return annotation;
    }

    private @Nullable PsiElement[] findMethodsInSameClass(PsiAnnotation annotation, @Nullable String methodName) {
        if (methodName == null) {
            return null;
        }

        PsiMethod annotatedMethod = PsiTreeUtil.getParentOfType(annotation, PsiMethod.class);
        if (annotatedMethod == null) {
            return null;
        }

        PsiClass owner = annotatedMethod.getContainingClass();
        if (owner == null) {
            return null;
        }

        List<PsiElement> hits = new ArrayList<>();
        for (PsiMethod method : owner.getMethods()) {
            if (Objects.equals(method.getName(), methodName)) {
                hits.add(method);
            }
        }

        if (hits.isEmpty()) {
            return null;
        }
        return hits.toArray(PsiElement[]::new);
    }

    private @Nullable String stringValue(@Nullable PsiElement expr) {
        if (expr instanceof PsiLiteralExpression literal) {
            Object value = literal.getValue();
            if (value instanceof String string) {
                return string;
            }
        }
        return null;
    }
}
