package unbosque.edu.co.aulasync.seguridad;

/**
 * Roles del sistema.
 * <ul>
 * <li>{@code ADMIN}: gestiona usuarios (CRUD).</li>
 * <li>{@code DIRECCION}: consulta usuarios, horarios y toda la información.</li>
 * <li>{@code COMITE}: edita horarios (drag and drop), docentes, cursos,
 * secciones, aulas y grupos.</li>
 * </ul>
 */
public enum Rol {
	ADMIN, DIRECCION, COMITE;

	/**
	 * Convierte un texto en rol, ignorando mayúsculas y espacios.
	 *
	 * @param valor texto del rol
	 * @return el rol, o {@code null} si el texto no corresponde a ninguno
	 */
	public static Rol desde(String valor) {
		if (valor == null) {
			return null;
		}
		for (Rol r : values()) {
			if (r.name().equalsIgnoreCase(valor.trim())) {
				return r;
			}
		}
		return null;
	}
}
