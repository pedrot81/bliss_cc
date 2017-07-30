package com.pdt.blissrecruitment.connector;

import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.exception.ConnectorException;

import java.util.List;

public interface IConnector {

    boolean checkServer() throws ConnectorException;

    boolean shareQuestion(String destinationEmail, String contentUrl) throws ConnectorException;

    Question retrieveQuestion(String questionId) throws ConnectorException;

    List<Question> questionsList(int limit, int offset) throws ConnectorException;

    List<Question> filteredQuestionsList(int limit, int offset, String filter) throws ConnectorException;

    Question updateQuestion(String questionId, String json) throws ConnectorException;
}


