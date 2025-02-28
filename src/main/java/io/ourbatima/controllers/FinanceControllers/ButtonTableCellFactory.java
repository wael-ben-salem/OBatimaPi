package io.ourbatima.controllers.FinanceControllers;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ButtonTableCellFactory <S> implements Callback<TableColumn<S, Void>, TableCell<S, Void>> {


    @Override
    public TableCell<S, Void> call(TableColumn<S, Void> param) {
        return new TableCell<S, Void>() {
            private final HBox container = new HBox(5); // HBox to hold buttons
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {

                updateButton.getStyleClass().add("update-button");
                deleteButton.getStyleClass().add("delete-button");

                // Set button actions
                updateButton.setOnAction(event -> {
                    S rowData = getTableView().getItems().get(getIndex());
                    handleUpdateAction(rowData);
                });

                deleteButton.setOnAction(event -> {
                    S rowData = getTableView().getItems().get(getIndex());
                    handleDeleteAction(rowData);
                });

                container.getChildren().addAll(updateButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }

            private void handleUpdateAction(S rowData) {
                // Handle update action
                System.out.println("Update button clicked for: " + rowData);
                // Add your update logic here
            }

            private void handleDeleteAction(S rowData) {
                // Handle delete action
                System.out.println("Delete button clicked for: " + rowData);
                // Add your delete logic here
            }
        };
    }
}
