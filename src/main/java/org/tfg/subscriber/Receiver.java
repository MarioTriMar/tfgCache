package org.tfg.subscriber;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.tfg.config.CacheEntries;
import org.tfg.controller.Manager;


public class Receiver implements MessageListener {
    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] data=message.toString().split("/");
        String port=Manager.get().getPort();
        if(!data[0].equals(port)) {
            logger.info("Consumed event {}", message);
            switch (data[1]) {
                case "saveCompany" -> Manager.get().getCacheEntries().clean("companies");
                case "updateCompany", "changeCompanyState" -> this.updateCompany(data[2]);
                case "saveCustomer" -> Manager.get().getCacheEntries().clean("customers");
                case "updateCustomer", "changeCustomerState" -> this.updateCustomer(data[2]);
                case "saveOrder" -> this.saveOrder(data[2], data[3]);
                case "saveProduct" -> {
                    Manager.get().getCacheEntries().evict("products", "allProducts");
                    Manager.get().getCacheEntries().evict("products", data[2]);
                }
                case "updateProduct", "changeStock" -> this.updateProduct(data[2]);
            }
        }
    }

    private void updateProduct(String companyId) {
        CacheEntries cacheEntries=Manager.get().getCacheEntries();
        cacheEntries.clean("orders");
        cacheEntries.clean("customersOrders");
        cacheEntries.clean("order");

        cacheEntries.evict("companiesOrders",companyId);
        cacheEntries.evict("products",companyId);
        cacheEntries.evict("products","allProducts");
    }

    private void updateCustomer(String customerId) {
        CacheEntries cacheEntries=Manager.get().getCacheEntries();
        cacheEntries.clean("orders");
        cacheEntries.clean("companiesOrders");
        cacheEntries.clean("order");
        cacheEntries.clean("customers");
        cacheEntries.evict("customersOrders", customerId);
    }

    private void updateCompany(String companyId){
        CacheEntries cacheEntries=Manager.get().getCacheEntries();
        cacheEntries.clean("companies");
        cacheEntries.clean("orders");
        cacheEntries.clean("order");
        cacheEntries.clean("customersOrders");
        cacheEntries.clean("product");

        cacheEntries.evict("products",companyId);
        cacheEntries.evict("companiesOrders", companyId);
        cacheEntries.evict("products","allProducts");
    }

    private void saveOrder(String companyId, String customerId){
        CacheEntries cacheEntries=Manager.get().getCacheEntries();
        cacheEntries.clean("orders");
        cacheEntries.clean("order");

        cacheEntries.evict("companiesOrders",companyId);
        cacheEntries.evict("customersOrders",customerId);
        cacheEntries.evict("money",customerId);
    }
}
