package io.jeyong.resilience4j.navigator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodToAnnotationResolver {

    private static final String CIRCUIT_BREAKER_FQN = "io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker";

    public @Nullable PsiElement[] resolveFromMethodIdentifier(PsiElement sourceElement) {
        if (!(sourceElement instanceof PsiIdentifier identifier)) {
            return null;
        }
        if (!(identifier.getParent() instanceof PsiMethod method)) {
            return null;
        }

        PsiClass owner = method.getContainingClass();
        if (owner == null) {
            return null;
        }

        List<PsiElement> hits = new ArrayList<>();
        for (var candidate : owner.getMethods()) {
            for (var annotation : candidate.getModifierList()
                    .getAnnotations()) {
                if (!CIRCUIT_BREAKER_FQN.equals(annotation.getQualifiedName())) {
                    continue;
                }

                var fallbackValue = annotation.findAttributeValue("fallbackMethod");
                if (fallbackValue instanceof PsiLiteralExpression literal) {
                    var fallbackName = literal.getValue();
                    if (fallbackName instanceof String name
                            && isValidFallback(method, candidate, name)) {
                        hits.add(fallbackValue);
                    }
                }
            }
        }

        if (hits.isEmpty()) {
            return null;
        }
        return hits.toArray(PsiElement[]::new);
    }

    private boolean isValidFallback(PsiMethod fallback, PsiMethod annotated, String methodName) {
        if (!Objects.equals(fallback.getName(), methodName)) {
            return false;
        }

        PsiParameter[] origParams = annotated.getParameterList()
                .getParameters();
        PsiParameter[] candParams = fallback.getParameterList()
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
}
