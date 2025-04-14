package uk.gov.companieshouse.acsp.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressDtoTests {

    @Test
    void correctlySetsAndGetsPremises() {
        AddressDto address = new AddressDto();
        address.setPremises("123");
        assertEquals("123", address.getPremises());
    }

    @Test
    void correctlySetsAndGetsAddressLine1() {
        AddressDto address = new AddressDto();
        address.setAddressLine1("Main Street");
        assertEquals("Main Street", address.getAddressLine1());
    }

    @Test
    void correctlySetsAndGetsAddressLine2() {
        AddressDto address = new AddressDto();
        address.setAddressLine2("Suite 4B");
        assertEquals("Suite 4B", address.getAddressLine2());
    }

    @Test
    void correctlySetsAndGetsLocality() {
        AddressDto address = new AddressDto();
        address.setLocality("Springfield");
        assertEquals("Springfield", address.getLocality());
    }

    @Test
    void correctlySetsAndGetsRegion() {
        AddressDto address = new AddressDto();
        address.setRegion("Illinois");
        assertEquals("Illinois", address.getRegion());
    }

    @Test
    void correctlySetsAndGetsCountry() {
        AddressDto address = new AddressDto();
        address.setCountry("USA");
        assertEquals("USA", address.getCountry());
    }

    @Test
    void correctlySetsAndGetsPostalCode() {
        AddressDto address = new AddressDto();
        address.setPostalCode("62704");
        assertEquals("62704", address.getPostalCode());
    }

    @Test
    void returnsNullForUnsetFields() {
        AddressDto address = new AddressDto();
        assertNull(address.getPremises());
        assertNull(address.getAddressLine1());
        assertNull(address.getAddressLine2());
        assertNull(address.getLocality());
        assertNull(address.getRegion());
        assertNull(address.getCountry());
        assertNull(address.getPostalCode());
    }

    @Test
    void equalsReturnsTrueForEqualObjects() {
        AddressDto address1 = new AddressDto();
        address1.setPremises("123");
        address1.setAddressLine1("Main Street");
        address1.setAddressLine2("Suite 4B");
        address1.setLocality("Springfield");
        address1.setRegion("Illinois");
        address1.setCountry("USA");
        address1.setPostalCode("62704");

        AddressDto address2 = new AddressDto();
        address2.setPremises("123");
        address2.setAddressLine1("Main Street");
        address2.setAddressLine2("Suite 4B");
        address2.setLocality("Springfield");
        address2.setRegion("Illinois");
        address2.setCountry("USA");
        address2.setPostalCode("62704");

        assertEquals(address1, address2);
    }

    @Test
    void equalsReturnsFalseForDifferentObjects() {
        AddressDto address1 = new AddressDto();
        address1.setPremises("123");
        address1.setAddressLine1("Main Street");

        AddressDto address2 = new AddressDto();
        address2.setPremises("456");
        address2.setAddressLine1("Elm Street");

        assertNotEquals(address1, address2);
    }

    @Test
    void hashCodeIsConsistentForEqualObjects() {
        AddressDto address1 = new AddressDto();
        address1.setPremises("123");
        address1.setAddressLine1("Main Street");

        AddressDto address2 = new AddressDto();
        address2.setPremises("123");
        address2.setAddressLine1("Main Street");

        assertEquals(address1.hashCode(), address2.hashCode());
    }
}