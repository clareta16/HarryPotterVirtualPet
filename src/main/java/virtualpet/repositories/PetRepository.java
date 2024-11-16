package virtualpet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import virtualpet.models.MyVirtualPet;

import java.util.List;

public interface PetRepository extends JpaRepository<MyVirtualPet, Long> {
    List<MyVirtualPet> findByOwnerUsername(String username);
    boolean existsByNameAndOwnerUsername(String name, String ownerUsername);

}