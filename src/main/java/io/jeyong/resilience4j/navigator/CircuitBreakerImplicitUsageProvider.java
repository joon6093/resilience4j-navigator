package io.jeyong.resilience4j.navigator;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class CircuitBreakerImplicitUsageProvider implements ImplicitUsageProvider {

    private static final String CIRCUIT_BREAKER_FQN =
            "io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker";

    @Override
    public boolean isImplicitUsage(@NotNull PsiElement element) {
        if (!(element instanceof PsiMethod method)) {
            return false;
        }
        var owner = method.getContainingClass();
        if (owner == null) {
            return false;
        }

        for (PsiMethod candidate : owner.getMethods()) {
            for (PsiAnnotation ann : candidate.getModifierList().getAnnotations()) {
                if (!CIRCUIT_BREAKER_FQN.equals(ann.getQualifiedName())) {
                    continue;
                }
                var value = ann.findAttributeValue("fallbackMethod");
                if (value instanceof PsiLiteralExpression lit
                        && method.getName().equals(lit.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        return false;
    }

    @Override
    public boolean isImplicitWrite(@NotNull PsiElement element) {
        return false;
    }
}
