package test.easymapper;

import autoparams.Repeat;
import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveType_specs {

    @AllArgsConstructor
    public static class BooleanBag {
        private final boolean value;

        public boolean getValue() {
            return value;
        }
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_correctly_converts_boolean_value(Mapper sut, BooleanBag source) {
        BooleanBag actual = sut.map(source, BooleanBag.class, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ByteBag {
        private final byte value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_byte_value(Mapper sut, ByteBag source) {
        ByteBag actual = sut.map(source, ByteBag.class, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ShortBag {
        private final short value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_short_value(Mapper sut, ShortBag source) {
        ShortBag actual = sut.map(source, ShortBag.class, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class FloatBag {
        private final float value;
    }

    @AutoParameterizedTest
    void suit_correctly_converts_float_value(Mapper sut, FloatBag source) {
        FloatBag actual = sut.map(source, FloatBag.class, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class DoubleBag {
        private final double value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_double_value(Mapper sut, DoubleBag source) {
        DoubleBag actual = sut.map(source, DoubleBag.class, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class CharBag {
        private final char value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_char_value(Mapper sut, CharBag source) {
        CharBag actual = sut.map(source, CharBag.class, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }
}
