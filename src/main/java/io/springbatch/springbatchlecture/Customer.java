package io.springbatch.springbatchlecture;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
public class Customer {

	@Id
	@GeneratedValue
	private Long id;
	private String firstName;
	private String lastName;
	private String birthdate;

}
