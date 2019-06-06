package com.productbot.model;

import com.messanger.Button;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.productbot.service.PostbackPayload.*;

@Getter
public enum Role {

	USER(new Button("Menu", MENU_PAYLOAD.name()),
			new Button("Create own product", CREATE_OWN_PAYLOAD.name())),

	PERSONAL(new Button("Create filling", CT_FILLING_PAYLOAD.name()),
			new Button("Create product", CT_PRODUCT_PAYLOAD.name()),
			new Button("Update product", UPDATE_PRODUCT_PAYLOAD.name())),////todo provide this feature


	COURIER(new Button("Orderings list", ORDERINGS_LIST_PAYLOAD.name())),

	ADMIN(new Button("Set role", SET_ROLE_PAYLOAD.name()));

	private List<Button> navigationButtons;

	Role(Button... buttons) {
		this.navigationButtons = Arrays.asList(buttons);
	}
}
