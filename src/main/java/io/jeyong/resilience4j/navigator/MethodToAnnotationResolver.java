package io.jeyong.resilience4j.navigator;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

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
        for (PsiMethod candidate : owner.getMethods()) {
            for (PsiAnnotation annotation : candidate.getModifierList().getAnnotations()) {
                if (!CIRCUIT_BREAKER_FQN.equals(annotation.getQualifiedName())) {
                    continue;
                }

                PsiElement fallbackValue = annotation.findAttributeValue("fallbackMethod");
                if (fallbackValue instanceof PsiLiteralExpression literal
                        && Objects.equals(method.getName(), literal.getValue())) {
                    hits.add(fallbackValue);
                }
            }
        }

        if (hits.isEmpty()) {
            return null;
        }
        return hits.toArray(PsiElement[]::new);
    }
}
