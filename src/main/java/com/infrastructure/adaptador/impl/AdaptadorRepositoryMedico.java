package com.infrastructure.adaptador.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.domain.dto.MedicoDTO;
import com.domain.exception.ExcepcionServicio;
import com.domain.model.Medico;
import com.infrastructure.mapper.MappersMedicos;
import com.infrastructure.repository.HospitalRepository;
import com.infrastructure.repository.MedicoRepository;

import lombok.AllArgsConstructor;

@Transactional
@Component
@AllArgsConstructor
public class AdaptadorRepositoryMedico {

	private final MappersMedicos mapperMedico;

	private final MedicoRepository medicoRepository;

	private final HospitalRepository hospitalRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	public List<MedicoDTO> buscarTodosM() {
		return medicoRepository.findAll().stream().map(medico -> mapperMedico.toDTOMedico(medico)).toList();
	}

	public String saveMedico(MedicoDTO dto) {
		Medico medico = mapperMedico.toEntityMedico(dto);
		medico.setPassword(passwordEncoder.encode(medico.getPassword()));
		return medicoRepository.save(medico).getNumLicencia();
	}

	public void eliminarMedico(String nLicencia) {
		if (!medicoRepository.existsById(nLicencia)) {
			throw new ExcepcionServicio(HttpStatus.NOT_FOUND, "El número de Licencia no existe");
		}
		medicoRepository.deleteById(nLicencia);
	}

	public MedicoDTO buscarMedico(String nLicencia) {
		return mapperMedico.toDTOMedico(medicoRepository.findById(nLicencia).orElse(null));
	}

	public List<MedicoDTO> buscarMedicoXEspecialidad(String especialidad, long idHospital) throws ExcepcionServicio {
		hospitalRepository.findById(idHospital).orElseThrow(() -> new ExcepcionServicio(HttpStatus.NOT_FOUND, "El hospital no existe"));
		List<MedicoDTO> listaMedicoDTO = medicoRepository.buscarMedicoXEspecialidad(especialidad, idHospital).stream().map(mapperMedico::toDTOMedico).toList();
		if (listaMedicoDTO.isEmpty()) {
			throw new ExcepcionServicio(HttpStatus.NOT_FOUND, "No hay medicos con esa especialidad");
		}
		return listaMedicoDTO;
	}

	public List<MedicoDTO> buscarMedicosXHospital(long idHospital) {
		List<MedicoDTO> listaMedicoDTO = medicoRepository.buscarMedicosXHospital(idHospital).stream().map(mapperMedico::toDTOMedico).toList();
		if (listaMedicoDTO.isEmpty()) {
			throw new ExcepcionServicio(HttpStatus.NOT_FOUND, "No hay medicos en el hospital");
		}
		return listaMedicoDTO;
	}

	public MedicoDTO buscarMiMedico(String nSS) {
		return mapperMedico.toDTOMedico(medicoRepository.buscarMmedico(nSS).orElseThrow(() -> new ExcepcionServicio(HttpStatus.NOT_FOUND, "Paciente con NSS no existe o no tiene citas")));
	}
}
