package org.bitvector.microservice_test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class DbPersister extends AbstractVerticle {

    private Logger logger;
    private SessionFactory sessionFactory;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbPersister");

        Configuration configuration = new Configuration()
                .setProperties(new Properties(System.getProperties()))
                .addAnnotatedClass(Product.class);                                  // SUPER FUCKING IMPORTANT
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        logger.info("Started a DbPersister...");
    }

    @Override
    public void stop() {
        sessionFactory.close();
        logger.info("Stopped a DbPersister...");
    }

    private void onMessage(Message<DbMessage> message) {
        switch (message.body().getAction()) {
            case "handleGetAllProducts":
                this.handleGetAllProducts(message);
                break;
            case "handleGetProductId":
                this.handleGetProductId(message);
                break;
            default:
                logger.error("Received message with an unknown action.");
                DbMessage dbResponse = new DbMessage(false, null);
                message.reply(dbResponse);
                break;
        }
    }

    private void handleGetAllProducts(Message<DbMessage> message) {
        Session session = sessionFactory.openSession();
        List objs = session.createQuery("FROM Product").list();
        session.close();

        DbMessage dbResponse = new DbMessage(true, objs);
        message.reply(dbResponse);
    }

    private void handleGetProductId(Message<DbMessage> message) {
        String id = (String) message.body().getParams().get(0);

        Session session = sessionFactory.openSession();
        List objs = session.createQuery("FROM Product WHERE id=:id")
                .setParameter("id", Integer.parseInt(id))
                .list();
        session.close();

        DbMessage dbResponse = new DbMessage(true, objs);
        message.reply(dbResponse);
    }

    private void handlePutProductId(Message<DbMessage> message) {
        // FIXME
    }

    private void handlePostProduct(Message<DbMessage> message) {
        // FIXME
    }

    private void handleDeleteProductId(Message<DbMessage> message) {
        // FIXME
    }
}
