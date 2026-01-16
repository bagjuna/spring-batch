package io.springbatch.springbatchlecture;


import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Customer2 {

	@Id
	private Long id;
	private String firstName;
	private String lastName;
	private String birthdate;
}
