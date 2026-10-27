package unbosque.edu.co.aulasync.dto;

/** Usuario sin contraseña. */
public record UsuarioResponseDto(Long id, String nombre, String correo, String rol) {
}
