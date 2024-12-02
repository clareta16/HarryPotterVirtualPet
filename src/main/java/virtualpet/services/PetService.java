package virtualpet.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import virtualpet.models.*;
import virtualpet.repositories.PetRepository;
import virtualpet.repositories.UserRepository;
import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<MyVirtualPet> getAllPets(UserDetails userDetails) {
        // Si és admin, retorna tots els pets. Si no, només els pets associats a l'usuari
        if (userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return petRepository.findAll();
        } else {
            return petRepository.findByOwnerUsername(userDetails.getUsername());
        }
    }

    public MyVirtualPet createYourPet(PetType petType, String name, Colour colour, UserDetails userDetails) {
        // Verificar si la mascota ja existeix per aquest propietari
        if (petRepository.existsByNameAndOwnerUsername(name, userDetails.getUsername())) {
            throw new RuntimeException("Pet with this name already exists for this user.");
        }

        MyVirtualPet pet = new MyVirtualPet(petType, name, colour);
        User petOwner = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        pet.setOwner(petOwner);
        return petRepository.save(pet);
    }

    public boolean hasAccessToPet(MyVirtualPet pet, UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = pet.getOwner() != null && pet.getOwner().getUsername().equals(userDetails.getUsername());
        return isAdmin || isOwner;
    }

    public MyVirtualPet getPetById(Long id, UserDetails userDetails) {
        MyVirtualPet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (hasAccessToPet(pet, userDetails)) {
            return pet;
        } else {
            throw new AccessDeniedException("You do not have permission to view this pet");
        }
    }

    public MyVirtualPet updatePet(Long id, PetAction petAction, UserDetails userDetails) {
        MyVirtualPet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (hasAccessToPet(existingPet, userDetails)) {
            // Processar l'acció seleccionada
            if (petAction != null) {
                switch (petAction) {
                    case EAT:
                        existingPet.eat();  // Cridar al mètode eat
                        break;
                    case TRAIN:
                        existingPet.train();  // Cridar al mètode train
                        break;
                    case SLEEP:
                        existingPet.sleep();  // Cridar al mètode sleep
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid action: " + petAction);
                }
            }

            // Guardem i retornem el pet actualitzat
            return petRepository.save(existingPet);
        } else {
            throw new AccessDeniedException("You do not have permission to update this pet");
        }
    }

    public boolean isUserAllowedToAccessPet(Long petId, Authentication authentication) {
        // Get user details from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Fetch the pet from the database
        MyVirtualPet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        // Check if the current user is the owner of the pet
        return pet.getOwner().equals(username);
    }


    public void deletePet(Long petId, UserDetails userDetails) {
        MyVirtualPet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (hasAccessToPet(pet, userDetails)) {
            petRepository.delete(pet);
        } else {
            throw new AccessDeniedException("You do not have permission to delete this pet");
        }
    }
}




