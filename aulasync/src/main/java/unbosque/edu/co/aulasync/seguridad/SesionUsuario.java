package unbosque.edu.co.aulasync.seguridad;

/**
 * Usuario autenticado de la petición en curso. El interceptor lo deja como
 * atributo de la petición para que los controladores sepan quién llama.
 */
public class SesionUsuario {

	private final Long id;
	private final Rol rol;

	public SesionUsuario(Long id, Rol rol) {
		this.id = id;
		this.rol = rol;
	}

	/** @return identificador del usuario */
	public Long getId() {
		return id;
	}

	/** @return rol vigente del usuario */
	public Rol getRol() {
		return rol;
	}
}
