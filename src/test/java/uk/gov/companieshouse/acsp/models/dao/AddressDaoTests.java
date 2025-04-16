package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressDaoTests {

    @Test
    void correctlySetsAndGetsPremises() {
        AddressDao address = new AddressDao();
        address.setPremises("123A");
        assertEquals("123A", address.getPremises());
    }

    @Test
    void correctlySetsAndGetsAddressLine1() {
        AddressDao address = new AddressDao();
        address.setAddressLine1("Main Street");
        assertEquals("Main Street", address.getAddressLine1());
    }

    @Test
    void correctlySetsAndGetsAddressLine2() {
        AddressDao address = new AddressDao();
        address.setAddressLine2("Suite 5");
        assertEquals("Suite 5", address.getAddressLine2());
    }

    @Test
    void correctlySetsAndGetsLocality() {
        AddressDao address = new AddressDao();
        address.setLocality("Springfield");
        assertEquals("Springfield", address.getLocality());
    }

    @Test
    void correctlySetsAndGetsRegion() {
        AddressDao address = new AddressDao();
        address.setRegion("Illinois");
        assertEquals("Illinois", address.getRegion());
    }

    @Test
    void correctlySetsAndGetsCountry() {
        AddressDao address = new AddressDao();
        address.setCountry("USA");
        assertEquals("USA", address.getCountry());
    }

    @Test
    void correctlySetsAndGetsPostalCode() {
        AddressDao address = new AddressDao();
        address.setPostalCode("62704");
        assertEquals("62704", address.getPostalCode());
    }

    @Test
    void returnsNullForUnsetFields() {
        AddressDao address = new AddressDao();
        assertNull(address.getPremises());
        assertNull(address.getAddressLine1());
        assertNull(address.getAddressLine2());
        assertNull(address.getLocality());
        assertNull(address.getRegion());
        assertNull(address.getCountry());
        assertNull(address.getPostalCode());
    }
}