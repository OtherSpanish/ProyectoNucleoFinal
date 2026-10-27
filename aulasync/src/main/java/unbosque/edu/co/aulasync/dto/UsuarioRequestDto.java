package unbosque.edu.co.aulasync.dto;

/**
 * Datos para crear o editar un usuario. {@code rol} es ADMIN, DIRECCION o
 * COMITE. En la edición, {@code password} en blanco conserva la actual.
 */
public record UsuarioRequestDto(String nombre, String correo, String password, String rol) {
}
