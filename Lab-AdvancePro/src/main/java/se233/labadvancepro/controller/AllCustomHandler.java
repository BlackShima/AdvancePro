package se233.labadvancepro.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import se233.labadvancepro.Launcher;
import se233.labadvancepro.model.character.BasedCharacter;
import se233.labadvancepro.model.item.Armor;
import se233.labadvancepro.model.item.BasedEquipment;
import se233.labadvancepro.model.item.Weapon;

import java.util.ArrayList;

public class AllCustomHandler {
    public static class GenCharacterHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Launcher.createNewCharacterAndResetEquipment();
           /* Launcher.setMainCharacter(GenCharacter.setUpCharacter());
            Launcher.refreshPane();*/
        }
    }
    public static void onDragDetected(MouseEvent event, BasedEquipment equipment, ImageView imgView) {
        Dragboard db = imgView.startDragAndDrop(TransferMode.ANY);
        db.setDragView(imgView.getImage());
        ClipboardContent content = new ClipboardContent();
        content.put(equipment.DATA_FORMAT, equipment);
        db.setContent(content);
        //แก้ให้ลากของแล้วไม่หาย
        imgView.setOnDragDone(dragEvent -> {
           if (dragEvent.getTransferMode() == null){
               imgView.setVisible(true);
           } else if (dragEvent.getTransferMode() == TransferMode.MOVE){
               onEquipDone(dragEvent);
           }
        });
        event.consume();
    }
    public static void onDragOver(DragEvent event, String type) {
        Dragboard dragboard = event.getDragboard();
        BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(BasedEquipment.DATA_FORMAT);
        BasedCharacter mainCharacter = Launcher.getMainCharacter();
        if (retrievedEquipment instanceof Weapon) {
            if (type.equals("Weapon")) { // ตรวจสอบว่าเป็นช่องอาวุธ
                // สำหรับ Battlemage: สามารถใส่อาวุธได้ทุกประเภท
                if (mainCharacter.getClass().getSimpleName().equals("BattleMageCharacter")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                // สำหรับตัวละคร Physical/Magical: ต้องตรงตาม DamageType
                else if (mainCharacter.getDamageType() == ((Weapon) retrievedEquipment).getDamageType()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
        } else if (retrievedEquipment instanceof Armor) {
            if (type.equals("Armor")) { // ตรวจสอบว่าเป็นช่องเกราะ
                // สำหรับ Battlemage: ห้ามติดตั้งชุดเกราะ
                if (mainCharacter.getClass().getSimpleName().equals("BattleMageCharacter")) {
                    // ไม่ต้องทำอะไร (ไม่ accept)
                }
                // สำหรับตัวละครอื่นๆ: สามารถติดตั้งชุดเกราะได้
                else {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
        }
        event.consume();
    }
    public static void onDragDropped(DragEvent event, Label lbl, StackPane imgGroup) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
        if(dragboard.hasContent(BasedEquipment.DATA_FORMAT)) {
            BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(BasedEquipment.DATA_FORMAT);
            BasedCharacter character = Launcher.getMainCharacter();
            if(retrievedEquipment.getClass().getSimpleName().equals("Weapon")) {
                if (Launcher.getEquippedWeapon() != null)
                    allEquipments.add(Launcher.getEquippedWeapon());
                Launcher.setEquippedWeapon((Weapon) retrievedEquipment);
                character.equipWeapon((Weapon) retrievedEquipment);
            } else {
                if (Launcher.getEquippedArmor() != null)
                    allEquipments.add(Launcher.getEquippedArmor());
                Launcher.setEquippedArmor((Armor) retrievedEquipment);
                character.equipArmor((Armor) retrievedEquipment);
            }
            Launcher.setMainCharacter(character);
            Launcher.setAllEquipments(allEquipments);
            Launcher.refreshPane();
            ImageView imgView = new ImageView();
            if (imgGroup.getChildren().size()!=1) {
                imgGroup.getChildren().remove(1);
                Launcher.refreshPane();
            }
            lbl.setText(retrievedEquipment.getClass().getSimpleName() + ":\n" + retrievedEquipment.getName());
            imgView.setImage(new Image(Launcher.class.getResource(retrievedEquipment.getImagepath()).toString()));
            imgGroup.getChildren().add(imgView);
            dragCompleted = true;
        }
        event.setDropCompleted(dragCompleted);
    }
    public static void onEquipDone(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
        BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(BasedEquipment.DATA_FORMAT);
        int pos =-1;
        for(int i=0; i<allEquipments.size() ; i++) {
            if (allEquipments.get(i).getName().equals(retrievedEquipment.getName())) {
                pos = i;
            }
        }
        if (pos !=-1) {
            allEquipments.remove(pos);
        }
        Launcher.setAllEquipments(allEquipments);
        Launcher.refreshPane();
    }
}