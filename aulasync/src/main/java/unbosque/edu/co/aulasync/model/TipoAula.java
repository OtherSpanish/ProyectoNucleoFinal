package unbosque.edu.co.aulasync.model;

/**
 * Tipo de espacio físico. Cada curso exige un tipo y el motor solo le asigna
 * aulas de ese tipo.
 *
 * @version 1.0
 */
public enum TipoAula {

	/** Salón tradicional con tablero y pupitres. */
	CONVENCIONAL,

	/** Sala con computadores, para cursos que los necesitan. */
	SALA_COMPUTO,

	/** Salón con mobiliario móvil, para trabajo en grupo o talleres. */
	MOBILIARIO_MOVIL
}
