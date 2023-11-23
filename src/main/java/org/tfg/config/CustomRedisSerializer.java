package org.tfg.config;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class CustomRedisSerializer extends JdkSerializationRedisSerializer {

    @Override
    public Object deserialize(byte[] bytes) {
        return super.deserialize(decompress(bytes));
    }

    @Override
    public byte[] serialize(Object object) {
        return compress(super.serialize(object));
    }

    private byte[] compress(byte[] content) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(content);
        } catch (IOException e) {
            throw new SerializationException("Unable to compress data", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] decompress(byte[] contentBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
        } catch (IOException e) {
            throw new SerializationException("Unable to decompress data", e);
        }
        return out.toByteArray();
    }
}