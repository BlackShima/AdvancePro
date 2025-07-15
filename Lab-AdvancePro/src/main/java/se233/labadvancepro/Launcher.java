package se233.labadvancepro;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import se233.labadvancepro.controller.GenCharacter;
import se233.labadvancepro.controller.GenItemList;
import se233.labadvancepro.model.character.BasedCharacter;
import se233.labadvancepro.model.item.Armor;
import se233.labadvancepro.model.item.BasedEquipment;
import se233.labadvancepro.model.item.Weapon;
import se233.labadvancepro.view.CharacterPane;
import se233.labadvancepro.view.EquipPane;
import se233.labadvancepro.view.InventoryPane;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Launcher extends Application {
    public static Scene getMainScene() {
        return mainScene;
    }

    public static void setMainScene(Scene mainScene) {
        Launcher.mainScene = mainScene;
    }

    private static Scene mainScene;
    private static BasedCharacter mainCharacter = null;

    private static ArrayList<BasedEquipment> allEquipments = null;

    public static Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public static void setEquippedWeapon(Weapon equippedWeapon) {
        Launcher.equippedWeapon = equippedWeapon;
    }

    private static Weapon equippedWeapon = null;

    public static Armor getEquippedArmor() {
        return equippedArmor;
    }

    public static void setEquippedArmor(Armor equippedArmor) {
        Launcher.equippedArmor = equippedArmor;
    }

    public static ArrayList<BasedEquipment> getAllEquipments() {
        return allEquipments;
    }

    public static void setAllEquipments(ArrayList<BasedEquipment> allEquipments) {
        Launcher.allEquipments = allEquipments;
    }

    private static Armor equippedArmor = null;
    private static CharacterPane characterPane = null;

    public static EquipPane getEquipPane() {
        return equipPane;
    }

    public static void setEquipPane(EquipPane equipPane) {
        Launcher.equipPane = equipPane;
    }

    private static EquipPane equipPane = null;

    public static InventoryPane getInventoryPane() {
        return inventoryPane;
    }

    public static void setInventoryPane(InventoryPane inventoryPane) {
        Launcher.inventoryPane = inventoryPane;
    }

    private static InventoryPane inventoryPane = null;
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Chapter1");
        primaryStage.setResizable(false);
        primaryStage.show();
        mainCharacter = GenCharacter.setUpCharacter();
        allEquipments = GenItemList.setUpItemList();
        Pane mainPane = getMainPane();
        mainScene = new Scene(mainPane);
        primaryStage.setScene(mainScene);
    }
    public Pane getMainPane() {
        BorderPane mainPane = new BorderPane();
        characterPane = new CharacterPane();
        equipPane = new EquipPane();
        inventoryPane = new InventoryPane();
        refreshPane();
        mainPane.setCenter(characterPane);
        mainPane.setLeft(equipPane);
        mainPane.setBottom(inventoryPane);
        return mainPane;
    }
    public static void createNewCharacterAndResetEquipment(){

        if(equippedWeapon != null){
            allEquipments.add(equippedWeapon);
        }
        if(equippedArmor != null){
            allEquipments.add(equippedArmor);
        }
        mainCharacter = GenCharacter.setUpCharacter();
        equippedWeapon = null;
        equippedArmor = null;

        refreshPane();
    }
    public static void refreshPane() {
        characterPane.drawPane(mainCharacter);
        equipPane.drawPane(equippedWeapon,equippedArmor);
        inventoryPane.drawPane(allEquipments);
    }

    public static BasedCharacter getMainCharacter() { return mainCharacter; }
    public static void setMainCharacter(BasedCharacter mainCharacter) {
        Launcher.mainCharacter = mainCharacter;
    }
    public static void main(String[] args) {
        launch(args);
    }
}