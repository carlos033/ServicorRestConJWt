/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose Tools | Templates and open the template in the editor.
 */
package com.proyecto.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.dto.CitaDTO;
import com.proyecto.dto.MedicoDTO;
import com.proyecto.excepciones.ExcepcionServicio;
import com.proyecto.modelos.Cita;
import com.proyecto.serviciosI.ServiciosCitaI;
import com.proyecto.utiles.Transformadores;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 *
 * @author ck
 */
@AllArgsConstructor
@RestController
@RequestMapping("/citas")
public class CitasJpaController {

	private final Transformadores transformador;

	private final ServiciosCitaI sCita;

	@GetMapping()
	public ResponseEntity<List<CitaDTO>> listarCitas() {
		return ResponseEntity.ok(sCita.buscarTodasC().stream().map(transformador::convertirADTOC).collect(Collectors.toList()));

	}

	@PostMapping
	public ResponseEntity<CitaDTO> aniadirCita(@Valid @RequestBody CitaDTO citaDto) {
		Cita convertedCita = transformador.convertirAEntidadC(citaDto);
		try {
			sCita.crearCita(convertedCita);
		} catch (ExcepcionServicio ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(transformador.convertirADTOC(convertedCita));
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> eliminarCita(@PathVariable int id) throws ExcepcionServicio {
		sCita.eliminarCita(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{nSS}/buscarMMedico")
	public ResponseEntity<MedicoDTO> buscarMiMedico(@PathVariable String nSS) throws ExcepcionServicio {
		return ResponseEntity.ok(transformador.convertirADTOM(sCita.buscarMiMedico(nSS)));

	}

	@GetMapping("/{id}/buscarXId")
	public ResponseEntity<CitaDTO> buscarXId(@PathVariable int id) throws ExcepcionServicio {
		return ResponseEntity.ok(transformador.convertirADTOC(sCita.buscarXId(id)));
	}
}
