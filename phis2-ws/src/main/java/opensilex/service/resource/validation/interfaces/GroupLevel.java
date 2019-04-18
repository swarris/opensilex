//******************************************************************************
//                              GroupLevel.java
// SILEX-PHIS
// Copyright © INRA 2018
// Creation date: 28 June 2018
// Contact: arnaud.charleroy@inra.fr, morgane.vidal@inra.fr, anne.tireau@inra.fr, 
//          pascal.neveu@inra.fr
//******************************************************************************
package opensilex.service.resource.validation.interfaces;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import opensilex.service.resource.validation.validator.GroupLevelValidator;

/**
 * Interface to check that the given group level is one of those expected.
 * @see GroupLevelValidator
 * @author Arnaud Charleroy <arnaud.charleroy@inra.fr>, Morgane Vidal <morgane.vidal@inra.fr>
 */
@Target(value={METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = GroupLevelValidator.class)
@Documented
public @interface GroupLevel {
    String message() default "is not a valid group level";
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
}
