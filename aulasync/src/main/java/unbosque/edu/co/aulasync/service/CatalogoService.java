package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import unbosque.edu.co.aulasync.model.Catalogos;
import unbosque.edu.co.aulasync.model.Dedicacion;
import unbosque.edu.co.aulasync.model.IntensidadHoraria;
import unbosque.edu.co.aulasync.model.JornadaInstitucional;
import unbosque.edu.co.aulasync.model.OpcionCatalogo;
import unbosque.edu.co.aulasync.model.Rol;
import unbosque.edu.co.aulasync.model.TipoAula;
import unbosque.edu.co.aulasync.motor.MotivoRechazo;

/**
 * Arma los catálogos a partir de los enums y constantes del dominio, para que
 * el frontend no repita datos como el tope de horas de una dedicación, las
 * sesiones de una intensidad o las horas de la jornada.
 *
 * @version 1.0
 */
@Service
public class CatalogoService {

	private static final Locale ESPANOL = Locale.of("es", "CO");

	/**
	 * @return todos los catálogos.
	 */
	public Catalogos obtener() {
		return new Catalogos(roles(), tiposAula(), dedicaciones(), intensidades(), dias(), horasJornada(), motivos());
	}

	private List<OpcionCatalogo> roles() {
		List<OpcionCatalogo> lista = new ArrayList<>();
		for (Rol rol : Rol.values()) {
			lista.add(new OpcionCatalogo(rol.name(), etiquetaRol(rol)));
		}
		return lista;
	}

	private String etiquetaRol(Rol rol) {
		if (rol == Rol.ADMIN) {
			return "Administrador";
		}
		if (rol == Rol.COMITE) {
			return "Comité táctico";
		}
		return "Docente";
	}

	private List<OpcionCatalogo> tiposAula() {
		List<OpcionCatalogo> lista = new ArrayList<>();
		for (TipoAula tipo : TipoAula.values()) {
			lista.add(new OpcionCatalogo(tipo.name(), etiquetaTipoAula(tipo)));
		}
		return lista;
	}

	private String etiquetaTipoAula(TipoAula tipo) {
		if (tipo == TipoAula.SALA_COMPUTO) {
			return "Sala de cómputo";
		}
		if (tipo == TipoAula.MOBILIARIO_MOVIL) {
			return "Mobiliario móvil";
		}
		return "Convencional";
	}

	private List<OpcionCatalogo> dedicaciones() {
		List<OpcionCatalogo> lista = new ArrayList<>();
		for (Dedicacion dedicacion : Dedicacion.values()) {
			OpcionCatalogo opcion = new OpcionCatalogo(dedicacion.name(), etiquetaDedicacion(dedicacion));
			opcion.setHorasMaximas(dedicacion.getHorasMaximas());
			lista.add(opcion);
		}
		return lista;
	}

	private String etiquetaDedicacion(Dedicacion dedicacion) {
		if (dedicacion == Dedicacion.TIEMPO_COMPLETO) {
			return "Tiempo completo";
		}
		if (dedicacion == Dedicacion.TRES_CUARTOS) {
			return "Tres cuartos de tiempo";
		}
		if (dedicacion == Dedicacion.MEDIO_TIEMPO) {
			return "Medio tiempo";
		}
		return "Un cuarto de tiempo";
	}

	private List<OpcionCatalogo> intensidades() {
		List<OpcionCatalogo> lista = new ArrayList<>();
		for (IntensidadHoraria intensidad : IntensidadHoraria.values()) {
			String sesiones = intensidad.getSesionesSemana() == 1 ? "1 sesión" : intensidad.getSesionesSemana() + " sesiones";
			OpcionCatalogo opcion = new OpcionCatalogo(intensidad.name(),
					sesiones + " × " + intensidad.getDuracionHoras() + " h");
			opcion.setSesionesSemana(intensidad.getSesionesSemana());
			opcion.setDuracionHoras(intensidad.getDuracionHoras());
			lista.add(opcion);
		}
		return lista;
	}

	private List<OpcionCatalogo> dias() {
		List<OpcionCatalogo> lista = new ArrayList<>();
		for (DayOfWeek dia : DayOfWeek.values()) {
			if (JornadaInstitucional.esDiaHabil(dia)) {
				String nombre = dia.getDisplayName(TextStyle.FULL, ESPANOL);
				OpcionCatalogo opcion = new OpcionCatalogo(dia.name(),
						nombre.substring(0, 1).toUpperCase(ESPANOL) + nombre.substring(1));
				opcion.setHoraCierre(JornadaInstitucional.horaCierre(dia));
				lista.add(opcion);
			}
		}
		return lista;
	}

	private List<Integer> horasJornada() {
		List<Integer> horas = new ArrayList<>();
		for (int hora = JornadaInstitucional.APERTURA; hora <= JornadaInstitucional.CIERRE_ENTRE_SEMANA; hora++) {
			horas.add(hora);
		}
		return horas;
	}

	private List<OpcionCatalogo> motivos() {
		List<OpcionCatalogo> lista = new ArrayList<>();
		for (MotivoRechazo motivo : MotivoRechazo.values()) {
			OpcionCatalogo opcion = new OpcionCatalogo(motivo.name(), motivo.getEtiqueta());
			opcion.setDescripcion(motivo.getDescripcion());
			lista.add(opcion);
		}
		return lista;
	}
}
