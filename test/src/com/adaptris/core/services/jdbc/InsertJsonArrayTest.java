package com.adaptris.core.services.jdbc;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.ServiceException;
import com.adaptris.core.jdbc.JdbcConnection;
import com.adaptris.core.json.jdbc.InsertJsonArray;
import com.adaptris.util.TimeInterval;

public class InsertJsonArrayTest extends JdbcJsonInsertCase {


  public InsertJsonArrayTest(String arg0) {
    super(arg0);
  }

  @Override
  protected Object retrieveObjectForSampleConfig() {
    InsertJsonArray service = createService();
    service.setTable("myTable");
    JdbcConnection connection = new JdbcConnection();
    connection.setConnectUrl("jdbc:mysql://localhost:3306/mydatabase");
    connection.setConnectionAttempts(2);
    connection.setConnectionRetryInterval(new TimeInterval(3L, "SECONDS"));
    service.setConnection(connection);
    return service;
  }


  public void testService() throws Exception {
    createDatabase();
    InsertJsonArray service = configure(createService());
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage(CONTENT);
    execute(service, msg);
    doAssert(3);
  }

  public void testService_NotArray() throws Exception {
    createDatabase();
    InsertJsonArray service = configure(createService());
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage(OBJECT_CONTENT);
    try {
      execute(service, msg);
      fail();
    } catch (ServiceException expected) {

    }
  }

  public void testService_BrokenColumn() throws Exception {
    createDatabase();
    InsertJsonArray service = configure(createService());
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage(INVALID_COLUMN_ARRAY);
    try {
      execute(service, msg);
      fail();
    } catch (ServiceException expected) {

    }
  }

  protected InsertJsonArray createService() {
    return new InsertJsonArray();
  }

}
