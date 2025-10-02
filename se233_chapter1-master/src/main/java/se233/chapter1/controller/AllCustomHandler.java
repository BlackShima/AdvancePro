package se233.chapter1.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import se233.chapter1.Launcher;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.character.BattleMageCharacter;
import se233.chapter1.model.item.Armor;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;
import java.util.ArrayList;

public class AllCustomHandler {
    public static class GenCharacterHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            ArrayList<BasedEquipment> inv = Launcher.getAllEquipments();
            Weapon oldW = Launcher.getEquippedWeapon();
            Armor oldA = Launcher.getEquippedArmor();
            if (oldW != null) inv.add(oldW);
            if (oldA != null) inv.add(oldA);

            Launcher.setEquippedWeapon(null);
            Launcher.setEquippedArmor(null);
            Launcher.setAllEquipments(inv);

            Launcher.setMainCharacter(GenCharacter.setUpCharacter());
            Launcher.refreshPane();
        }
    }
    public static void onDragDetected(MouseEvent event, BasedEquipment equipment, ImageView imgView) {
        Dragboard db = imgView.startDragAndDrop(TransferMode.ANY);
        db.setDragView(imgView.getImage());
        ClipboardContent content = new ClipboardContent();
        content.put(equipment.DATA_FORMAT, equipment);
        db.setContent(content);
        event.consume();
    }

    public static void onDragOver(DragEvent event, String slotType) {
        Dragboard db = event.getDragboard();
        if (!db.hasContent(BasedEquipment.DATA_FORMAT)) return;

        BasedEquipment eq = (BasedEquipment) db.getContent(BasedEquipment.DATA_FORMAT);
        BasedCharacter character = Launcher.getMainCharacter();

        boolean allowed = false;
        if ("Weapon".equals(slotType) && eq instanceof Weapon) {
            if (character instanceof se233.chapter1.model.character.BattleMageCharacter || ((Weapon) eq).getDamageType() == character.getType()) {
                allowed = true;
            }
        }
        else if ("Armor".equals(slotType) && eq instanceof Armor) {
            if (!(character instanceof se233.chapter1.model.character.BattleMageCharacter)) {
                allowed = true;
            }
        }
        if (allowed) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }
    public static void onDragDropped(DragEvent event, Label lbl, StackPane imgGroup) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT)) {
            BasedEquipment retrieved = (BasedEquipment) dragboard.getContent(BasedEquipment.DATA_FORMAT);
            BasedCharacter character = Launcher.getMainCharacter();
            boolean legal = false;
            if (retrieved instanceof Weapon) {
                Weapon w = (Weapon) retrieved;
                if (character instanceof BattleMageCharacter || w.getDamageType() == character.getType()) {
                    legal = true;
                }
            } else if (retrieved instanceof Armor) {
                if (!(character instanceof BattleMageCharacter)) {
                    legal = true;
                }
            }
            if (!legal) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }
            ArrayList<BasedEquipment> inv = Launcher.getAllEquipments();
            inv.removeIf(eq -> eq.getName().equals(retrieved.getName()));

            if (retrieved instanceof Weapon) {
                Weapon oldW = Launcher.getEquippedWeapon();
                if (oldW != null) inv.add(oldW);
                Launcher.setEquippedWeapon((Weapon) retrieved);
                character.equipWeapon((Weapon) retrieved);
            } else {
                Armor oldA = Launcher.getEquippedArmor();
                if (oldA != null) inv.add(oldA);
                Launcher.setEquippedArmor((Armor) retrieved);
                character.equipArmor((Armor) retrieved);
            }

            Launcher.setAllEquipments(inv);
            Launcher.setMainCharacter(character);

            lbl.setText(retrieved.getClass().getSimpleName() + ":\n" + retrieved.getName());
            imgGroup.getChildren().removeIf(node -> node instanceof ImageView && node != imgGroup.getChildren().get(0));
            imgGroup.getChildren().add(new ImageView(new Image(Launcher.class.getResource(retrieved.getImagepath()).toString())));

            dragCompleted = true;
            Launcher.refreshPane();
        }

        event.setDropCompleted(dragCompleted);
        event.consume();
    }

    public static void onEquipDone(DragEvent event) {
        event.consume();
    }

    public static class UnequipAllHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            BasedCharacter character = Launcher.getMainCharacter();
            ArrayList<BasedEquipment> inv = Launcher.getAllEquipments();
            Weapon oldW = character.unequipWeapon();
            Armor oldA = character.unequipArmor();
            if (oldW != null) inv.add(oldW);
            if (oldA != null) inv.add(oldA);
            Launcher.setEquippedWeapon(null);
            Launcher.setEquippedArmor(null);
            Launcher.setAllEquipments(inv);
            Launcher.refreshPane();
        }
    }
}