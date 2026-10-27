package unbosque.edu.co.aulasync.dto;

/** Credenciales de inicio de sesión ({@code POST /api/auth/login}). */
public record LoginRequestDto(String correo, String password) {
}
