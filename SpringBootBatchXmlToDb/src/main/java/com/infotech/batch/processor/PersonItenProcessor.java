package com.infotech.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.infotech.batch.model.Person;

public class PersonItenProcessor implements ItemProcessor<Person, Person>{

	/**
	 * Il s'agit d'une simple fonction Java qui prend un objet Person en entrée et renvoie le même objet en sortie.
	 *
	 * @param person Le paramètre d'entrée de type Person qui est traité par la méthode. La méthode renvoie simplement le même
	 * objet Person sans aucune modification.
	 * @return Le même objet `Person` qui a été passé en entrée est renvoyé.
	 */
	@Override
	public Person process(Person person) throws Exception {
		return person;
	}
}
