package org.bitvector.microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DbMessageCodec implements MessageCodec<DbMessage, DbMessage> {

    private Logger logger;
    private ObjectMapper jsonMapper;

    DbMessageCodec() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.DbMessageCodec");
        jsonMapper = new ObjectMapper();
    }

    @Override
    public void encodeToWire(Buffer buffer, DbMessage dbMessage) {
        try {
            buffer.appendBytes(jsonMapper.writeValueAsBytes(dbMessage));
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert message to wire format", e);
        }
    }

    @Override
    public DbMessage decodeFromWire(int pos, Buffer buffer) {
        DbMessage dbMessage = null;
        try {
            dbMessage = jsonMapper.readValue(buffer.getBytes(), DbMessage.class);
        } catch (IOException e) {
            logger.error("Failed to convert message from wire format", e);
        }
        return dbMessage;
    }

    @Override
    public DbMessage transform(DbMessage dbMessage) {
        return dbMessage;
    }

    @Override
    public String name() {
        return "DbMessageCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}
