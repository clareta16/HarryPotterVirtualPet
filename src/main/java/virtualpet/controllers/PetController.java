package virtualpet.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import virtualpet.models.MyVirtualPet;
import virtualpet.models.PetType;
import virtualpet.repositories.PetRepository;
import virtualpet.services.PetService;
import java.util.List;

@Tag(name = "Pet Controller", description = "Endpoints to handle pet interactions")
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final PetRepository petRepository;

    @Autowired
    public PetController(PetService petService, PetRepository petRepository) {
        this.petService = petService;
        this.petRepository = petRepository;
    }

    @Operation(summary = "Create a pet", description = "Crea a new pet to start training him")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet successfully created",
                    content = @Content(schema = @Schema(implementation = MyVirtualPet.class))),
            @ApiResponse(responseCode = "400", description = "Invalid details")
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<MyVirtualPet> createPet(@RequestParam PetType petType, @RequestParam String name, @RequestParam String colour) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MyVirtualPet newPet = petService.createYourPet(petType, name, colour, userDetails);
        return ResponseEntity.ok(newPet);
    }

    @Operation(summary = "Get all pets", description = "Get all pets from a specific user")
    @ApiResponse(responseCode = "200", description = "List of pets given correctly",
            content = @Content(schema = @Schema(implementation = MyVirtualPet.class)))
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<MyVirtualPet>> getAllPets() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<MyVirtualPet> pets = petService.getAllPets(userDetails);
        return ResponseEntity.ok(pets);
    }

    @Operation(summary = "Update your pet", description = "Update your pet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update made correctly",
                    content = @Content(schema = @Schema(implementation = MyVirtualPet.class))),
            @ApiResponse(responseCode = "404", description = "Could not find the pet"),
            @ApiResponse(responseCode = "403", description = "Not authorized to do this update")
    })
    @PreAuthorize("hasRole('ROLE_USER') and @petService.hasAccessToPet(#updatedPet, authentication)")  // Verificar l'accés amb el servei
    @PutMapping("/{petId}")
    public ResponseEntity<MyVirtualPet> updatePet(@PathVariable Long petId, @RequestBody MyVirtualPet updatedPet) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MyVirtualPet updatedPetResponse = petService.updatePet(petId, updatedPet, userDetails);  // Passant a petService per actualitzar el pet
        if (updatedPetResponse != null) {
            return ResponseEntity.ok(updatedPetResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get the pet by id", description = "Returns the pet by the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet returned successfully",
                    content = @Content(schema = @Schema(implementation = MyVirtualPet.class))),
            @ApiResponse(responseCode = "404", description = "Could not find the pet"),
            @ApiResponse(responseCode = "403", description = "Not authorized to interact with this pet")
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{petId}")
    public ResponseEntity<MyVirtualPet> getPet(@PathVariable Long petId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MyVirtualPet pet = petService.getPetById(petId, userDetails);
        if (pet != null) {
            return ResponseEntity.ok(pet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a pet", description = "Delete one of your pets. You will not be able to interact with it anymore")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet deleted correctly"),
            @ApiResponse(responseCode = "403", description = "Not authorized to interact with this pet")
    })
    @PreAuthorize("hasRole('ROLE_USER') and @petService.hasAccessToPet(#petId, authentication)")
    @DeleteMapping("/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable Long petId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        petService.deletePet(petId, userDetails);
        return ResponseEntity.ok("Pet successfully deleted");
    }
}




