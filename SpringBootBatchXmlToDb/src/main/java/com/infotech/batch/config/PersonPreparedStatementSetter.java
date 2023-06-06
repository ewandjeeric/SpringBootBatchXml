package com.infotech.batch.config;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.infotech.batch.model.Person;

public class PersonPreparedStatementSetter implements ItemPreparedStatementSetter<Person> {


	/**
	 * Cette fonction définit les valeurs d'un objet PreparedStatement avec les propriétés d'un objet Person.
	 *
	 * @param person L'objet personne qui contient les données à définir dans le PreparedStatement.
	 * @param ps Le paramètre "ps" est un objet PreparedStatement utilisé pour exécuter une instruction SQL avec les valeurs
	 * définies par la méthode "setValues". La méthode définit les valeurs des paramètres dans l'objet PreparedStatement à
	 * l'aide des valeurs de l'objet "person".
	 */
	@Override
	public void setValues(Person person, PreparedStatement ps) throws SQLException {
		ps.setInt(1, person.getPersonId());
		ps.setString(2, person.getFirstName());
		ps.setString(3, person.getLastName());
		ps.setString(4, person.getEmail());
		ps.setInt(5, person.getAge());
	}

}
