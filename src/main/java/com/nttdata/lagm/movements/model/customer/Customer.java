package com.nttdata.lagm.movements.model.customer;

import java.io.Serializable;

import lombok.Data;

@Data
public class Customer implements Serializable {
	private String id;
	private String lastName;
	private String firstName;
	private String dni;
	private String phone;
	private String email;
	private Integer customerTypeId;
}
