package one.digitalinnovation.personaapi.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.personaapi.dto.MessageResponseDTO;
import one.digitalinnovation.personaapi.dto.request.PersonDTO;
import one.digitalinnovation.personaapi.entity.Person;
import one.digitalinnovation.personaapi.exception.PersonNotFoundException;
import one.digitalinnovation.personaapi.mapper.PersonMapper;
import one.digitalinnovation.personaapi.repository.PersonRepository;

@Service
public class PersonService {

	private PersonRepository personRepository;

	private final PersonMapper personMapper = PersonMapper.INSTANCE;

	@Autowired
	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public MessageResponseDTO createPerson(PersonDTO personDTO) {
		Person personToSave = personMapper.toModel(personDTO);

		Person savedPerson = personRepository.save(personToSave);
		return createMessageResponse(savedPerson.getId(), "Created person with ID ");
	}

	public List<PersonDTO> listAll() {
		List<Person> allPeople = personRepository.findAll();
		return allPeople.stream().map(personMapper::toDTO).collect(Collectors.toList());
	}

	public PersonDTO findById(Long id) throws PersonNotFoundException {
		Person person = verifyIfExists(id);
		return personMapper.toDTO(person);
	}

	public void delete(Long id) throws PersonNotFoundException {
		verifyIfExists(id);
		personRepository.deleteById(id);
	}

	public MessageResponseDTO updateById(Long id, @Valid PersonDTO personDTO) throws PersonNotFoundException {
		verifyIfExists(id);
		
		Person personToUpdate = personMapper.toModel(personDTO);

		Person updatedPerson = personRepository.save(personToUpdate);
		return createMessageResponse(updatedPerson.getId(), "Updated person with ID ");
	}

	private Person verifyIfExists(Long id) throws PersonNotFoundException {
		return personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException("Person not found with ID " + id));
	}
	
	private MessageResponseDTO createMessageResponse(Long id, String messsage) {
		return MessageResponseDTO
				.builder()
				.message(messsage + id)
				.build();
	}
}
