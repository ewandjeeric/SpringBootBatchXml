package com.infotech.batch.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.infotech.batch.model.Person;
import com.infotech.batch.processor.PersonItenProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	

	// une instance de `JobBuilderFactory` dans la classe `BatchConfig`. `JobBuilderFactory` est une usine pour créer des
	// instances `Job`, qui sont utilisées pour définir un travail par lots dans Spring Batch. En injectant
	// `JobBuilderFactory`, la classe `BatchConfig` peut créer et configurer des tâches à exécuter.
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	

	// une instance de `StepBuilderFactory` dans la classe `BatchConfig`. `StepBuilderFactory` est une usine pour créer des
	// instances `Step`, qui sont utilisées pour définir des étapes individuelles dans un travail Spring Batch. En injectant
	// `StepBuilderFactory`, la classe `BatchConfig` peut créer et configurer des étapes à utiliser dans une tâche.
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	

	// objet `DataSource` dans la classe `BatchConfig`. Le `DataSource` est utilisé pour se connecter à une base de données et
	// est requis pour que `JdbcBatchItemWriter` écrive des données dans la base de données.
	@Autowired
	private DataSource dataSource;
	
	/**
	 * Cette fonction crée une nouvelle instance de la classe PersonItenProcessor à utiliser comme processeur dans une
	 * application Java.
	 *
	 * @return Une nouvelle instance de la classe `PersonItenProcessor` est renvoyée.
	 */
	@Bean
	public PersonItenProcessor processor(){
		return new PersonItenProcessor();
	}
	
	/**
	 * Cette fonction crée un StaxEventItemReader pour lire les données d'un fichier XML et utilise XStreamMarshaller pour
	 * désorganiser les données dans des objets Person.
	 *
	 * @return Un objet StaxEventItemReader configuré pour lire des objets Person à partir d'un fichier XML à l'aide de
	 * XStreamMarshaller pour le démarshalling.
	 */
	@Bean
	public StaxEventItemReader<Person> reader(){
		StaxEventItemReader<Person> reader = new StaxEventItemReader<Person>();
		reader.setResource(new ClassPathResource("persons.xml"));
		reader.setFragmentRootElementName("person");
		
		Map<String,String> aliasesMap =new HashMap<String,String>();
		aliasesMap.put("person", "com.infotech.batch.model.Person");
		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliasesMap);
		
		reader.setUnmarshaller(marshaller);
		return reader;
	}
	
	/**
	 * Cette fonction crée un objet JdbcBatchItemWriter qui écrit des objets Person dans une base de données à l'aide
	 * d'instructions SQL et d'un PersonPreparedStatementSetter.
	 *
	 * @return Un objet JdbcBatchItemWriter<Person> est renvoyé.
	 */
	@Bean
	public JdbcBatchItemWriter<Person> writer(){
		JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
		writer.setDataSource(dataSource);
		writer.setSql("INSERT INTO person(person_id,first_name,last_name,email,age) VALUES(?,?,?,?,?)");
		writer.setItemPreparedStatementSetter(new PersonPreparedStatementSetter());
		return writer;
	}
	
	/**
	 * Cette fonction crée une étape dans un travail Spring Batch qui lit, traite et écrit des données par tranches de 100.
	 *
	 * @return Un objet Step est renvoyé.
	 */
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1").<Person,Person>
				chunk(1).
				reader(reader()).
				processor(processor()).
				writer(writer()).
				build();
	}

	/**
	 * Cette fonction crée une tâche pour exporter des données relatives à une personne.
	 *
	 * @return Un travail Spring Batch nommé "importPersonJob" avec un RunIdIncrementer et une seule étape nommée "step1".
	 */
	@Bean
	public Job exportPerosnJob(){
		return jobBuilderFactory.get("importPersonJob").
				incrementer(new RunIdIncrementer()).
				flow(step1()).end().
				build();
	}
}
