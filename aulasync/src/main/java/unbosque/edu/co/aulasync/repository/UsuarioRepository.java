package unbosque.edu.co.aulasync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Usuario;
import unbosque.edu.co.aulasync.model.Rol;

/**
 * Acceso a datos de la tabla {@code usuario}. Solo lo usa
 * {@code UsuarioService}.
 *
 * @version 1.1
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/**
	 * Busca un usuario por su correo.
	 *
	 * @param correo correo a buscar.
	 * @return el usuario, o vacío si no existe.
	 */
	Optional<Usuario> findByCorreo(String correo);

	/**
	 * Indica si ya hay un usuario con ese correo.
	 *
	 * @param correo correo a verificar.
	 * @return {@code true} si el correo ya está registrado.
	 */
	boolean existsByCorreo(String correo);

	/**
	 * Indica si el correo pertenece a un usuario distinto del indicado. Se usa al
	 * actualizar, para no chocar consigo mismo.
	 *
	 * @param correo correo a verificar.
	 * @param id     id del usuario que se está actualizando.
	 * @return {@code true} si otro usuario ya tiene ese correo.
	 */
	boolean existsByCorreoAndIdNot(String correo, Long id);

	/**
	 * Indica si existe al menos un usuario con el rol dado.
	 *
	 * @param rol rol a verificar.
	 * @return {@code true} si hay al menos un usuario con ese rol.
	 */
	boolean existsByRol(Rol rol);

	/**
	 * Cuenta los usuarios con el rol dado.
	 *
	 * @param rol rol a contar.
	 * @return cantidad de usuarios con ese rol.
	 */
	long countByRol(Rol rol);

	/**
	 * Lista todos los usuarios ordenados por nombre.
	 *
	 * @return usuarios en orden alfabético.
	 */
	List<Usuario> findAllByOrderByNombreAsc();

	/**
	 * Lista los usuarios de un rol, ordenados por nombre.
	 *
	 * @param rol rol a filtrar.
	 * @return usuarios del rol en orden alfabético.
	 */
	List<Usuario> findByRolOrderByNombreAsc(Rol rol);
}
