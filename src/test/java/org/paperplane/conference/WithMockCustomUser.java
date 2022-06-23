package org.paperplane.conference;

import org.paperplane.conference.model.Role;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_METHOD)
public @interface WithMockCustomUser {
    String id();
    String username();
    Role[] roles() default { Role.USER };
}
