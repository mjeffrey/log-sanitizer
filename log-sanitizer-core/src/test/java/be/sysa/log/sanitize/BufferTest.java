package be.sysa.log.sanitize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BufferTest {
    public static final String ORIGINAL = "12345678901234567890";
    private Buffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new Buffer(ORIGINAL);
    }

    @Test
    void testOriginalAndUnmodified() {
        buffer = new Buffer(ORIGINAL);
        assertThat(  buffer.toString() ).isEqualTo(ORIGINAL);
        assertThat(  buffer.getOriginal() ).isEqualTo(ORIGINAL);
    }

    @Test
    void testMask() {
        buffer.mask(3,2);
        assertThat(  buffer.toString()  ).isEqualTo("123**678901234567890");
    }

    @Test
    void testMaskString() {
        buffer.maskString("XYZ", new Bounds(3, 7));
        assertThat(  buffer.toString()  ).isEqualTo("123XYZ  901234567890");
    }

    @Test
    void testMaskCharacterBetween() {
        buffer.maskCharactersBetween( new Bounds(3, 18), 2, 1);
        assertThat(  buffer.toString()  ).isEqualTo("12345************890");
    }

    @Test
    void testMaskExceedingBounds() {
        buffer.mask(0,20);
        assertThat(  buffer.toString()  ).isEqualTo("********************");
    }
}
