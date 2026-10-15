package unbosque.edu.co.aulasync.motor;

import java.util.EnumMap;
import java.util.Map;

public class Sesion {
	private final int id;
	private final int claveCurso; // misma clave = mismo curso y sección (RD6)
	private final int docenteId;
	private final int grupoId;
	private final int duracionHoras;
	private final TipoAula tipoAula;
	private final int capacidadRequerida;

	private Franja franja;
	private AulaMotor aula;
	private MotorHorario.Motivo causaSinUbicar;
	private Map<MotorHorario.Motivo, Integer> desgloseCausas = new EnumMap<>(MotorHorario.Motivo.class);

	public Sesion(int id, int claveCurso, int docenteId, int grupoId, int duracionHoras, TipoAula tipoAula,
			int capacidadRequerida) {
		this.id = id;
		this.claveCurso = claveCurso;
		this.docenteId = docenteId;
		this.grupoId = grupoId;
		this.duracionHoras = duracionHoras;
		this.tipoAula = tipoAula;
		this.capacidadRequerida = capacidadRequerida;
	}

	public int getId() {
		return id;
	}

	public int getClaveCurso() {
		return claveCurso;
	}

	public int getDocenteId() {
		return docenteId;
	}

	public int getGrupoId() {
		return grupoId;
	}

	public int getDuracionHoras() {
		return duracionHoras;
	}

	public TipoAula getTipoAula() {
		return tipoAula;
	}

	public int getCapacidadRequerida() {
		return capacidadRequerida;
	}

	public Franja getFranja() {
		return franja;
	}

	public void setFranja(Franja franja) {
		this.franja = franja;
	}

	public AulaMotor getAula() {
		return aula;
	}

	public void setAula(AulaMotor aula) {
		this.aula = aula;
	}

	public MotorHorario.Motivo getCausaSinUbicar() {
		return causaSinUbicar;
	}

	public void setCausaSinUbicar(MotorHorario.Motivo m) {
		this.causaSinUbicar = m;
	}

	public Map<MotorHorario.Motivo, Integer> getDesgloseCausas() {
		return desgloseCausas;
	}

	public void setDesgloseCausas(Map<MotorHorario.Motivo, Integer> d) {
		this.desgloseCausas = d;
	}
}