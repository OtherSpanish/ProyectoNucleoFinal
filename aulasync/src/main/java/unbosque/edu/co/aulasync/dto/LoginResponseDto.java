package unbosque.edu.co.aulasync.dto;

/** Respuesta del login: token para la cabecera Bearer y datos del usuario. */
public record LoginResponseDto(String token, Long id, String nombre, String correo, String rol) {
}
