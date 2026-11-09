package unbosque.edu.co.aulasync.exception;

import java.time.DayOfWeek;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import unbosque.edu.co.aulasync.model.JornadaInstitucional;
import unbosque.edu.co.aulasync.model.RespuestaError;

/**
 * Punto único de manejo de excepciones del sistema.
 *
 * <p>
 * Cumple dos funciones:
 * </p>
 * <ul>
 * <li><b>Lanzar:</b> sus métodos estáticos {@code verificar...} validan los
 * datos y lanzan la excepción correspondiente. Es el único lugar donde se
 * instancian excepciones; los servicios llaman a estos métodos en lugar de
 * usar {@code throw new}.</li>
 * <li><b>Responder:</b> como {@link RestControllerAdvice}, intercepta esas
 * excepciones (y las de Spring y Spring Security) y las convierte en
 * respuestas JSON uniformes ({@link RespuestaError}) con el código HTTP
 * adecuado. Los controladores no capturan excepciones: solo las declaran con
 * {@code throws}.</li>
 * </ul>
 *
 * <p>
 * En cada etapa se agregan aquí los métodos {@code verificar...} de las nuevas
 * reglas.
 * </p>
 *
 * @version 1.15
 */
@RestControllerAdvice
public class LanzadorException {

	/** Longitud mínima de una contraseña. */
	public static final int MIN_LONGITUD_PASSWORD = 8;

	private static final Logger LOG = LoggerFactory.getLogger(LanzadorException.class);

	/**
	 * Constructor público requerido por Spring para registrar el manejador.
	 */
	public LanzadorException() {
		// Spring crea la instancia; los métodos de validación son estáticos.
	}

	// ------------------------------------------------------------------
	// Validaciones generales
	// ------------------------------------------------------------------

	/**
	 * Verifica que un registro buscado en la base de datos exista.
	 *
	 * @param entidad       resultado de la búsqueda (puede ser {@code null}).
	 * @param nombreEntidad nombre legible de la entidad (por ejemplo "Docente").
	 * @param id            identificador buscado.
	 * @throws EntidadNoEncontradaException si {@code entidad} es {@code null}.
	 */
	public static void verificarEntidadExiste(Object entidad, String nombreEntidad, Long id)
			throws EntidadNoEncontradaException {
		if (entidad == null) {
			throw new EntidadNoEncontradaException(nombreEntidad, id);
		}
	}

	/**
	 * Verifica que un campo de texto obligatorio no sea nulo ni esté vacío.
	 *
	 * @param valor valor recibido.
	 * @param campo nombre del campo, para el mensaje.
	 * @throws CampoObligatorioException si el valor es nulo o está vacío.
	 */
	public static void verificarCampoObligatorio(String valor, String campo) throws CampoObligatorioException {
		if (valor == null || valor.isBlank()) {
			throw new CampoObligatorioException(campo);
		}
	}

	/**
	 * Verifica que un campo obligatorio que no es texto (enum, número, objeto)
	 * no sea nulo.
	 *
	 * @param valor valor recibido.
	 * @param campo nombre del campo, para el mensaje.
	 * @throws CampoObligatorioException si el valor es nulo.
	 */
	public static void verificarObjetoObligatorio(Object valor, String campo) throws CampoObligatorioException {
		if (valor == null) {
			throw new CampoObligatorioException(campo);
		}
	}

	/**
	 * Verifica que una lista obligatoria no sea nula ni esté vacía.
	 *
	 * @param lista lista recibida.
	 * @param campo nombre del campo, para el mensaje.
	 * @throws CampoObligatorioException si la lista es nula o vacía.
	 */
	public static void verificarListaNoVacia(List<?> lista, String campo) throws CampoObligatorioException {
		if (lista == null || lista.isEmpty()) {
			throw new CampoObligatorioException(campo);
		}
	}

	/**
	 * Verifica que un número esté dentro de un rango (ambos extremos incluidos).
	 *
	 * @param valor  valor recibido.
	 * @param campo  nombre del campo, para el mensaje.
	 * @param minimo valor mínimo permitido.
	 * @param maximo valor máximo permitido.
	 * @throws DatoFueraDeRangoException si el valor está fuera del rango.
	 */
	public static void verificarRango(int valor, String campo, int minimo, int maximo)
			throws DatoFueraDeRangoException {
		if (valor < minimo || valor > maximo) {
			throw new DatoFueraDeRangoException(campo, minimo, maximo);
		}
	}

	/**
	 * Verifica que un nombre no esté ya en uso dentro de una entidad.
	 *
	 * @param yaExiste {@code true} si otro registro ya usa ese nombre.
	 * @param entidad  nombre legible de la entidad, para el mensaje.
	 * @param nombre   nombre verificado, para el mensaje.
	 * @throws NombreDuplicadoException si {@code yaExiste} es {@code true}.
	 */
	public static void verificarNombreDisponible(boolean yaExiste, String entidad, String nombre)
			throws NombreDuplicadoException {
		if (yaExiste) {
			throw new NombreDuplicadoException(entidad, nombre);
		}
	}

	/**
	 * Verifica que un código no esté ya en uso dentro de una entidad.
	 *
	 * @param yaExiste {@code true} si otro registro ya usa ese código.
	 * @param entidad  nombre legible de la entidad, para el mensaje.
	 * @param codigo   código verificado, para el mensaje.
	 * @throws CodigoDuplicadoException si {@code yaExiste} es {@code true}.
	 */
	public static void verificarCodigoDisponible(boolean yaExiste, String entidad, String codigo)
			throws CodigoDuplicadoException {
		if (yaExiste) {
			throw new CodigoDuplicadoException(entidad, codigo);
		}
	}

	// ------------------------------------------------------------------
	// Validaciones de seguridad y usuarios
	// ------------------------------------------------------------------

	/**
	 * Verifica que exista el usuario que Spring Security intenta cargar a partir
	 * del correo del token.
	 *
	 * @param usuario resultado de la búsqueda (puede ser {@code null}).
	 * @param correo  correo buscado.
	 * @throws UsernameNotFoundException si {@code usuario} es {@code null}.
	 */
	public static void verificarUsuarioSeguridadExiste(Object usuario, String correo)
			throws UsernameNotFoundException {
		if (usuario == null) {
			throw new UsernameNotFoundException("No existe un usuario con el correo " + correo + ".");
		}
	}

	/**
	 * Verifica el resultado de comparar las credenciales de un inicio de sesión.
	 *
	 * @param validas {@code true} si el correo existe y la contraseña coincide.
	 * @throws CredencialesInvalidasException si {@code validas} es
	 *                                        {@code false}.
	 */
	public static void verificarCredenciales(boolean validas) throws CredencialesInvalidasException {
		if (!validas) {
			throw new CredencialesInvalidasException();
		}
	}

	/**
	 * Verifica que la contraseña tenga al menos
	 * {@value #MIN_LONGITUD_PASSWORD} caracteres, una letra y un número.
	 *
	 * @param password contraseña en texto plano.
	 * @throws PasswordInvalidaException si no cumple la política.
	 */
	public static void verificarPassword(String password) throws PasswordInvalidaException {
		boolean tieneLetra = false;
		boolean tieneNumero = false;
		if (password != null) {
			for (int i = 0; i < password.length(); i++) {
				char c = password.charAt(i);
				if (Character.isLetter(c)) {
					tieneLetra = true;
				} else if (Character.isDigit(c)) {
					tieneNumero = true;
				}
			}
		}
		boolean largoValido = password != null && password.length() >= MIN_LONGITUD_PASSWORD;
		if (!largoValido || !tieneLetra || !tieneNumero) {
			throw new PasswordInvalidaException(MIN_LONGITUD_PASSWORD);
		}
	}

	/**
	 * Verifica que un correo no esté ya en uso.
	 *
	 * @param yaExiste {@code true} si el correo pertenece a otro usuario.
	 * @param correo   correo verificado, para el mensaje.
	 * @throws CorreoDuplicadoException si {@code yaExiste} es {@code true}.
	 */
	public static void verificarCorreoDisponible(boolean yaExiste, String correo) throws CorreoDuplicadoException {
		if (yaExiste) {
			throw new CorreoDuplicadoException(correo);
		}
	}

	/**
	 * Verifica que una operación no deje el sistema sin administradores.
	 *
	 * @param afectaUltimoAdmin {@code true} si la operación elimina o degrada al
	 *                          único administrador.
	 * @throws UltimoAdminException si {@code afectaUltimoAdmin} es {@code true}.
	 */
	public static void verificarNoEsUltimoAdmin(boolean afectaUltimoAdmin) throws UltimoAdminException {
		if (afectaUltimoAdmin) {
			throw new UltimoAdminException();
		}
	}

	/**
	 * Verifica que una operación sobre un usuario no rompa su vínculo con un
	 * docente.
	 *
	 * @param afectaDocente {@code true} si se quita el rol DOCENTE o se elimina
	 *                      un usuario vinculado a un docente.
	 * @param correo        correo del usuario, para el mensaje.
	 * @throws UsuarioConDocenteException si {@code afectaDocente} es
	 *                                    {@code true}.
	 */
	public static void verificarUsuarioSinDocente(boolean afectaDocente, String correo)
			throws UsuarioConDocenteException {
		if (afectaDocente) {
			throw new UsuarioConDocenteException(correo);
		}
	}

	// ------------------------------------------------------------------
	// Validaciones de docentes, especialidades y franjas
	// ------------------------------------------------------------------

	/**
	 * Verifica que el usuario que se vincula a un docente tenga rol DOCENTE.
	 *
	 * @param esDocente {@code true} si el usuario tiene rol DOCENTE.
	 * @param correo    correo del usuario, para el mensaje.
	 * @throws UsuarioNoEsDocenteException si {@code esDocente} es
	 *                                     {@code false}.
	 */
	public static void verificarUsuarioEsDocente(boolean esDocente, String correo)
			throws UsuarioNoEsDocenteException {
		if (!esDocente) {
			throw new UsuarioNoEsDocenteException(correo);
		}
	}

	/**
	 * Verifica que el usuario no esté vinculado ya a otro docente.
	 *
	 * @param yaVinculado {@code true} si otro docente ya usa ese usuario.
	 * @param correo      correo del usuario, para el mensaje.
	 * @throws UsuarioYaVinculadoException si {@code yaVinculado} es
	 *                                     {@code true}.
	 */
	public static void verificarUsuarioDisponible(boolean yaVinculado, String correo)
			throws UsuarioYaVinculadoException {
		if (yaVinculado) {
			throw new UsuarioYaVinculadoException(correo);
		}
	}

	/**
	 * Verifica que una especialidad no esté asignada antes de eliminarla.
	 *
	 * @param enUso  {@code true} si algún docente o curso la tiene.
	 * @param nombre nombre de la especialidad, para el mensaje.
	 * @throws EspecialidadEnUsoException si {@code enUso} es {@code true}.
	 */
	public static void verificarEspecialidadSinUso(boolean enUso, String nombre) throws EspecialidadEnUsoException {
		if (enUso) {
			throw new EspecialidadEnUsoException(nombre);
		}
	}

	/**
	 * Verifica que una franja (de docente o de sede) caiga en un día hábil, que
	 * la hora de inicio sea menor que la de fin y que esté dentro de la
	 * {@link JornadaInstitucional}.
	 *
	 * @param dia    día de la semana.
	 * @param inicio hora de inicio (incluida).
	 * @param fin    hora de fin (excluida).
	 * @throws FranjaInvalidaException si alguna de las condiciones falla.
	 */
	public static void verificarFranjaEnJornada(DayOfWeek dia, int inicio, int fin) throws FranjaInvalidaException {
		String franja = dia + " " + inicio + "-" + fin;
		if (!JornadaInstitucional.esDiaHabil(dia)) {
			throw new FranjaInvalidaException(franja + ": no hay clases el domingo.");
		}
		if (inicio >= fin) {
			throw new FranjaInvalidaException(franja + ": la hora de inicio debe ser menor que la de fin.");
		}
		if (inicio < JornadaInstitucional.APERTURA) {
			throw new FranjaInvalidaException(
					franja + ": la jornada empieza a las " + JornadaInstitucional.APERTURA + ".");
		}
		int cierre = JornadaInstitucional.horaCierre(dia);
		if (fin > cierre) {
			throw new FranjaInvalidaException(franja + ": ese día la jornada termina a las " + cierre + ".");
		}
	}

	/**
	 * Verifica que dos franjas consecutivas del mismo día no se crucen.
	 *
	 * @param seCruzan {@code true} si las franjas se traslapan.
	 * @param primera  descripción de la primera franja.
	 * @param segunda  descripción de la segunda franja.
	 * @throws FranjasTraslapadasException si {@code seCruzan} es {@code true}.
	 */
	public static void verificarSinTraslape(boolean seCruzan, String primera, String segunda)
			throws FranjasTraslapadasException {
		if (seCruzan) {
			throw new FranjasTraslapadasException(primera, segunda);
		}
	}

	/**
	 * Verifica que el usuario autenticado esté vinculado a un docente.
	 *
	 * @param docente docente encontrado por el correo (puede ser {@code null}).
	 * @throws AccesoDocenteException si {@code docente} es {@code null}.
	 */
	public static void verificarDocenteVinculado(Object docente) throws AccesoDocenteException {
		if (docente == null) {
			throw new AccesoDocenteException("Tu usuario no está vinculado a ningún docente.");
		}
	}

	/**
	 * Verifica que el grupo consultado lo dicte el docente autenticado.
	 *
	 * @param esSuyo {@code true} si el grupo es del docente.
	 * @throws AccesoDocenteException si {@code esSuyo} es {@code false}.
	 */
	public static void verificarGrupoDelDocente(boolean esSuyo) throws AccesoDocenteException {
		if (!esSuyo) {
			throw new AccesoDocenteException("Solo puedes consultar los grupos que dictas.");
		}
	}

	// ------------------------------------------------------------------
	// Validaciones de sedes, aulas y semestres
	// ------------------------------------------------------------------

	/**
	 * Verifica que una sede no tenga aulas antes de eliminarla.
	 *
	 * @param tieneAulas {@code true} si la sede tiene al menos un aula.
	 * @param nombre     nombre de la sede, para el mensaje.
	 * @throws SedeEnUsoException si {@code tieneAulas} es {@code true}.
	 */
	public static void verificarSedeSinAulas(boolean tieneAulas, String nombre) throws SedeEnUsoException {
		if (tieneAulas) {
			throw new SedeEnUsoException(nombre);
		}
	}

	/**
	 * Verifica que un número de semestre no esté ya registrado.
	 *
	 * @param yaExiste {@code true} si otro semestre ya tiene ese número.
	 * @param numero   número verificado, para el mensaje.
	 * @throws SemestreDuplicadoException si {@code yaExiste} es {@code true}.
	 */
	public static void verificarSemestreDisponible(boolean yaExiste, int numero) throws SemestreDuplicadoException {
		if (yaExiste) {
			throw new SemestreDuplicadoException(numero);
		}
	}

	/**
	 * Verifica que un semestre no tenga cursos antes de eliminarlo.
	 *
	 * @param tieneCursos {@code true} si el semestre tiene al menos un curso.
	 * @param numero      número del semestre, para el mensaje.
	 * @throws SemestreEnUsoException si {@code tieneCursos} es {@code true}.
	 */
	public static void verificarSemestreSinUso(boolean tieneCursos, int numero) throws SemestreEnUsoException {
		if (tieneCursos) {
			throw new SemestreEnUsoException(numero);
		}
	}

	// ------------------------------------------------------------------
	// Validaciones de grupos, inscripciones y horario
	// ------------------------------------------------------------------

	/**
	 * Verifica que un número de grupo no esté ya en uso en el curso.
	 *
	 * @param yaExiste    {@code true} si el curso ya tiene ese número.
	 * @param codigoCurso código del curso, para el mensaje.
	 * @param numero      número verificado, para el mensaje.
	 * @throws GrupoDuplicadoException si {@code yaExiste} es {@code true}.
	 */
	public static void verificarGrupoDisponible(boolean yaExiste, String codigoCurso, int numero)
			throws GrupoDuplicadoException {
		if (yaExiste) {
			throw new GrupoDuplicadoException(codigoCurso, numero);
		}
	}

	/**
	 * Verifica que un grupo no supere el máximo de inscritos.
	 *
	 * @param cantidad cantidad de inscritos que tendría.
	 * @param maximo   máximo permitido.
	 * @param grupo    descripción del grupo, para el mensaje.
	 * @throws GrupoLlenoException si {@code cantidad > maximo}.
	 */
	public static void verificarCupoGrupo(int cantidad, int maximo, String grupo) throws GrupoLlenoException {
		if (cantidad > maximo) {
			throw new GrupoLlenoException(grupo, maximo, cantidad);
		}
	}

	/**
	 * Verifica que el docente tenga la especialidad que exige el curso.
	 *
	 * @param tieneEspecialidad {@code true} si el docente la tiene.
	 * @param docente           nombre del docente, para el mensaje.
	 * @param curso             nombre del curso, para el mensaje.
	 * @param especialidad      especialidad exigida, para el mensaje.
	 * @throws DocenteSinEspecialidadException si {@code tieneEspecialidad} es
	 *                                         {@code false}.
	 */
	public static void verificarDocenteTieneEspecialidad(boolean tieneEspecialidad, String docente, String curso,
			String especialidad) throws DocenteSinEspecialidadException {
		if (!tieneEspecialidad) {
			throw new DocenteSinEspecialidadException(docente, curso, especialidad);
		}
	}

	/**
	 * Verifica que la carga semanal del docente no supere el tope de su
	 * dedicación.
	 *
	 * @param horas   horas semanales que tendría.
	 * @param tope    tope de su dedicación.
	 * @param docente nombre del docente, para el mensaje.
	 * @throws CargaDocenteExcedidaException si {@code horas > tope}.
	 */
	public static void verificarCargaDocente(int horas, int tope, String docente)
			throws CargaDocenteExcedidaException {
		if (horas > tope) {
			throw new CargaDocenteExcedidaException(docente, horas, tope);
		}
	}

	/**
	 * Verifica que la disponibilidad registrada del docente alcance para lo que
	 * se le quiere asignar.
	 *
	 * @param alcanza {@code true} si la disponibilidad es suficiente.
	 * @param docente nombre del docente, para el mensaje.
	 * @param detalle explicación de lo que falta.
	 * @throws DisponibilidadDocenteInsuficienteException si {@code alcanza} es
	 *                                                     {@code false}.
	 */
	public static void verificarDisponibilidadDocente(boolean alcanza, String docente, String detalle)
			throws DisponibilidadDocenteInsuficienteException {
		if (!alcanza) {
			throw new DisponibilidadDocenteInsuficienteException(docente, detalle);
		}
	}

	/**
	 * Verifica que un registro no tenga grupos antes de eliminarlo.
	 *
	 * @param tieneGrupos {@code true} si del registro dependen grupos.
	 * @param descripcion descripción del registro (por ejemplo "el curso
	 *                    ISIS-1101").
	 * @throws EntidadConGruposException si {@code tieneGrupos} es {@code true}.
	 */
	public static void verificarSinGrupos(boolean tieneGrupos, String descripcion) throws EntidadConGruposException {
		if (tieneGrupos) {
			throw new EntidadConGruposException(descripcion);
		}
	}

	/**
	 * Verifica que un estudiante no esté ya en otro grupo de la misma materia.
	 *
	 * @param estaEnOtro  {@code true} si ya está inscrito en otro grupo del
	 *                    curso.
	 * @param estudiante  nombre del estudiante, para el mensaje.
	 * @param curso       nombre del curso, para el mensaje.
	 * @param numeroGrupo número del otro grupo, para el mensaje.
	 * @throws EstudianteEnOtroGrupoException si {@code estaEnOtro} es
	 *                                        {@code true}.
	 */
	public static void verificarEstudianteSinOtroGrupo(boolean estaEnOtro, String estudiante, String curso,
			int numeroGrupo) throws EstudianteEnOtroGrupoException {
		if (estaEnOtro) {
			throw new EstudianteEnOtroGrupoException(estudiante, curso, numeroGrupo);
		}
	}

	/**
	 * Verifica que un movimiento manual del horario cumpla todas las reglas.
	 *
	 * @param valido   {@code true} si no falló ninguna regla.
	 * @param mensajes explicación de cada regla incumplida.
	 * @throws MovimientoInvalidoException si {@code valido} es {@code false}.
	 */
	public static void verificarMovimientoValido(boolean valido, List<String> mensajes)
			throws MovimientoInvalidoException {
		if (!valido) {
			throw new MovimientoInvalidoException(mensajes);
		}
	}

	// ------------------------------------------------------------------
	// Manejo (convierte excepciones en respuestas HTTP)
	// ------------------------------------------------------------------

	/**
	 * Maneja datos inválidos (cualquier subclase de {@link ValidacionException}).
	 *
	 * @param ex excepción de validación.
	 * @return respuesta 400 con el mensaje de la regla incumplida.
	 */
	@ExceptionHandler(ValidacionException.class)
	public ResponseEntity<RespuestaError> manejarValidacion(ValidacionException ex) {
		return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	/**
	 * Maneja registros inexistentes.
	 *
	 * @param ex excepción de entidad no encontrada.
	 * @return respuesta 404 con el mensaje correspondiente.
	 */
	@ExceptionHandler(EntidadNoEncontradaException.class)
	public ResponseEntity<RespuestaError> manejarNoEncontrada(EntidadNoEncontradaException ex) {
		return construir(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	/**
	 * Maneja operaciones que chocan con el estado actual de los datos (cualquier
	 * subclase de {@link ConflictoException}).
	 *
	 * @param ex excepción de conflicto.
	 * @return respuesta 409 con el mensaje correspondiente.
	 */
	@ExceptionHandler(ConflictoException.class)
	public ResponseEntity<RespuestaError> manejarConflicto(ConflictoException ex) {
		return construir(HttpStatus.CONFLICT, ex.getMessage());
	}

	/**
	 * Maneja un inicio de sesión con correo o contraseña incorrectos.
	 *
	 * @param ex excepción de credenciales inválidas.
	 * @return respuesta 401 con el mensaje correspondiente.
	 */
	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<RespuestaError> manejarCredenciales(CredencialesInvalidasException ex) {
		return construir(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	/**
	 * Maneja consultas de un docente sobre información que no le corresponde.
	 *
	 * @param ex excepción de acceso de docente.
	 * @return respuesta 403 con el mensaje correspondiente.
	 */
	@ExceptionHandler(AccesoDocenteException.class)
	public ResponseEntity<RespuestaError> manejarAccesoDocente(AccesoDocenteException ex) {
		return construir(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	/**
	 * Maneja peticiones sin token, con token inválido o vencido.
	 *
	 * @param ex excepción de autenticación de Spring Security.
	 * @return respuesta 401 con un mensaje genérico.
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<RespuestaError> manejarNoAutenticado(AuthenticationException ex) {
		return construir(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión o tu sesión expiró.");
	}

	/**
	 * Maneja operaciones que el rol del usuario no tiene permitidas.
	 *
	 * @param ex excepción de acceso denegado de Spring Security.
	 * @return respuesta 403 con un mensaje genérico.
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<RespuestaError> manejarAccesoDenegado(AccessDeniedException ex) {
		return construir(HttpStatus.FORBIDDEN, "Tu rol no tiene permiso para realizar esta operación.");
	}

	/**
	 * Maneja cuerpos JSON mal formados o con valores que no se pueden convertir
	 * (por ejemplo un día inexistente).
	 *
	 * @param ex excepción de lectura del cuerpo de la petición.
	 * @return respuesta 400 con un mensaje genérico.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<RespuestaError> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
		return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es inválido o tiene valores no permitidos.");
	}

	/**
	 * Maneja parámetros de ruta o de consulta con tipo incorrecto (por ejemplo
	 * {@code /api/docentes/abc} o {@code ?tipo=OTRO}).
	 *
	 * @param ex excepción de conversión del parámetro.
	 * @return respuesta 400 indicando el parámetro inválido.
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<RespuestaError> manejarParametroInvalido(MethodArgumentTypeMismatchException ex) {
		return construir(HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' tiene un valor inválido.");
	}

	/**
	 * Maneja rutas que no existen en la API.
	 *
	 * @param ex excepción de recurso no encontrado.
	 * @return respuesta 404 con un mensaje genérico.
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<RespuestaError> manejarRutaInexistente(NoResourceFoundException ex) {
		return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe.");
	}

	/**
	 * Maneja peticiones con un método HTTP no soportado por la ruta.
	 *
	 * @param ex excepción de método no soportado.
	 * @return respuesta 405 indicando el método rechazado.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<RespuestaError> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
		return construir(HttpStatus.METHOD_NOT_ALLOWED,
				"El método " + ex.getMethod() + " no está permitido en esta ruta.");
	}

	/**
	 * Maneja violaciones de restricciones de la base de datos que no cubre
	 * ninguna regla de negocio.
	 *
	 * @param ex excepción de integridad de datos.
	 * @return respuesta 409 con un mensaje genérico.
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<RespuestaError> manejarIntegridad(DataIntegrityViolationException ex) {
		return construir(HttpStatus.CONFLICT, "La operación viola una restricción de integridad de los datos.");
	}

	/**
	 * Último recurso para cualquier error no previsto. Registra el detalle en el
	 * log y no lo expone al cliente.
	 *
	 * @param ex excepción no controlada.
	 * @return respuesta 500 con un mensaje genérico.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<RespuestaError> manejarInesperada(Exception ex) {
		LOG.error("Error no controlado", ex);
		return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intenta de nuevo más tarde.");
	}

	private ResponseEntity<RespuestaError> construir(HttpStatus estado, String mensaje) {
		RespuestaError cuerpo = new RespuestaError(estado.value(), estado.getReasonPhrase(), mensaje);
		return ResponseEntity.status(estado).body(cuerpo);
	}
}
