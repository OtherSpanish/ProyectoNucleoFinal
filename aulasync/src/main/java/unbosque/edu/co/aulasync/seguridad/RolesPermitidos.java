package unbosque.edu.co.aulasync.seguridad;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restringe un endpoint (o un controlador completo) a los roles indicados. Si
 * un endpoint no lleva esta anotación, basta con estar autenticado. La
 * anotación del método tiene prioridad sobre la de la clase.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RolesPermitidos {

	/** @return roles que pueden invocar el endpoint */
	Rol[] value();
}
