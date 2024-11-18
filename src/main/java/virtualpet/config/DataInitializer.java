package virtualpet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import virtualpet.models.*;
import virtualpet.repositories.PetRepository;
import virtualpet.repositories.UserRepository;

import java.util.Collections;


@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PetRepository petRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner init() {
        return args -> {
            // Creació d'usuari admin si no existeix
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(Collections.singleton(Role.ROLE_ADMIN));  // Usar enum per als rols
                userRepository.save(admin);
                System.out.println("Admin user successfully created.");
            }

            // Si no hi ha cap mascota al repositori, en creo una de prova i li assigno l'admin com a propietari
            if (petRepository.count() == 0) {
                // Recuperem l'usuari admin
                User admin = userRepository.findByUsername("admin")
                        .orElseThrow(() -> new RuntimeException("Admin user not found"));

                // Crear la mascota
                MyVirtualPet demonstrationPet = new MyVirtualPet(PetType.PHOENIX, "Fawkes", Colour.GREEN);

                // Assignem l'usuari admin com a propietari
                demonstrationPet.setOwner(admin);

                // Guardem la mascota amb el propietari assignat
                petRepository.save(demonstrationPet);

                System.out.println("Pet for demonstration successfully created.");
            }
        };
    }
}
