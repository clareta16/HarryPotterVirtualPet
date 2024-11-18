package virtualpet.models;


import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Random;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyVirtualPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int hungryLevel;
    private int sleepLevel;
    private int combatLevel;
    private boolean isReadyToFightDarkLord;


    private int trainingCount = 0;

    @Enumerated(EnumType.STRING)
    private PetType petType;

    @Enumerated(EnumType.STRING)
    private Colour colour;

    @ManyToOne
    private User owner;

    private static final Random random = new Random();

    public MyVirtualPet(PetType petType, String name, Colour colour) {
        this.name = name;
        this.colour = colour;
        this.petType = petType;
        this.hungryLevel = 100;
        this.sleepLevel = 100;
        this.combatLevel = 0;
        this.isReadyToFightDarkLord = false;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void eat() {
        this.hungryLevel = Math.min(this.hungryLevel + 10, 100);
        this.sleepLevel = Math.max(this.sleepLevel - 10, 0);
    }

    public void train() {
        this.sleepLevel = Math.max(this.sleepLevel - 20, 0);
        this.hungryLevel = Math.max(this.hungryLevel - 30, 0);
        this.combatLevel = Math.min(this.combatLevel + 10, 100);

        this.trainingCount++;

        if (this.trainingCount >= 5) {
            this.isReadyToFightDarkLord = true;
        }
    }

    public void sleep() {
        this.sleepLevel = Math.min(this.sleepLevel + 30, 100);
        this.hungryLevel = Math.max(this.hungryLevel - 30, 0);
    }
}

