package unbosque.edu.co.aulasync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Docente;

/**
 * Acceso a datos de la tabla {@code docente}.
 *
 * @version 1.0
 */
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

	/**
	 * Indica si un usuario ya está vinculado a algún docente.
	 *
	 * @param usuarioId id del usuario.
	 * @return {@code true} si ya está vinculado.
	 */
	boolean existsByUsuarioId(Long usuarioId);

	/**
	 * Indica si un usuario está vinculado a un docente distinto del indicado. Se
	 * usa al actualizar.
	 *
	 * @param usuarioId id del usuario.
	 * @param id        id del docente que se está actualizando.
	 * @return {@code true} si otro docente ya usa ese usuario.
	 */
	boolean existsByUsuarioIdAndIdNot(Long usuarioId, Long id);

	/**
	 * Indica si alguna especialidad está asignada al menos a un docente.
	 *
	 * @param especialidadId id de la especialidad.
	 * @return {@code true} si algún docente la tiene.
	 */
	boolean existsByEspecialidadesId(Long especialidadId);

	/**
	 * Lista todos los docentes ordenados por el nombre de su usuario.
	 *
	 * @return docentes en orden alfabético.
	 */
	List<Docente> findAllByOrderByUsuarioNombreAsc();

	/**
	 * Lista los docentes que tienen una especialidad, en orden alfabético.
	 *
	 * @param especialidadId id de la especialidad.
	 * @return docentes con esa especialidad.
	 */
	List<Docente> findByEspecialidadesIdOrderByUsuarioNombreAsc(Long especialidadId);

	/**
	 * Busca el docente vinculado a un correo. Se usará para que el docente
	 * consulte sus propias clases.
	 *
	 * @param correo correo del usuario.
	 * @return el docente, o vacío si el correo no está vinculado a ninguno.
	 */
	Optional<Docente> findByUsuarioCorreo(String correo);
}
