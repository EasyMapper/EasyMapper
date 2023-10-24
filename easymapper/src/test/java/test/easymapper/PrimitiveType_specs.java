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

    @Getter
    @AllArgsConstructor
    public static class BoxedBooleanBag {
        private final Boolean value;
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_correctly_converts_boolean_value(Mapper sut, BooleanBag source) {
        BooleanBag actual = sut.map(source, BooleanBag.class, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_correctly_converts_boxed_boolean_value(
        Mapper sut,
        BoxedBooleanBag source
    ) {
        BoxedBooleanBag actual = sut.map(
            source,
            BoxedBooleanBag.class,
            BoxedBooleanBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_correctly_converts_boolean_value_to_boxed_boolean_value(
        Mapper sut,
        BooleanBag source
    ) {
        BoxedBooleanBag actual = sut.map(
            source,
            BooleanBag.class,
            BoxedBooleanBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    @Repeat(10)
    void sut_correctly_converts_boxed_boolean_value_to_boolean_value(
        Mapper sut,
        BoxedBooleanBag source
    ) {
        BooleanBag actual = sut.map(
            source,
            BoxedBooleanBag.class,
            BooleanBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ByteBag {
        private final byte value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedByteBag {
        private final Byte value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_byte_value(Mapper sut, ByteBag source) {
        ByteBag actual = sut.map(source, ByteBag.class, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_byte_value(
        Mapper sut,
        BoxedByteBag source
    ) {
        BoxedByteBag actual = sut.map(
            source,
            BoxedByteBag.class,
            BoxedByteBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_byte_value_to_boxed_byte_value(
        Mapper sut,
        ByteBag source
    ) {
        BoxedByteBag actual = sut.map(
            source,
            ByteBag.class,
            BoxedByteBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_byte_value_to_byte_value(
        Mapper sut,
        BoxedByteBag source
    ) {
        ByteBag actual = sut.map(
            source,
            BoxedByteBag.class,
            ByteBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ShortBag {
        private final short value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedShortBag {
        private final Short value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_short_value(Mapper sut, ShortBag source) {
        ShortBag actual = sut.map(source, ShortBag.class, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_short_value(
        Mapper sut,
        BoxedShortBag source
    ) {
        BoxedShortBag actual = sut.map(
            source,
            BoxedShortBag.class,
            BoxedShortBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_short_value_to_boxed_short_value(
        Mapper sut,
        ShortBag source
    ) {
        BoxedShortBag actual = sut.map(
            source,
            ShortBag.class,
            BoxedShortBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_short_value_to_short_value(
        Mapper sut,
        BoxedShortBag source
    ) {
        ShortBag actual = sut.map(
            source,
            BoxedShortBag.class,
            ShortBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class IntBag {
        private final int value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedIntBag {
        private final Integer value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_int_value(Mapper sut, IntBag source) {
        IntBag actual = sut.map(source, IntBag.class, IntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_int_value(
        Mapper sut,
        BoxedIntBag source
    ) {
        BoxedIntBag actual = sut.map(
            source,
            BoxedIntBag.class,
            BoxedIntBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_int_value_to_boxed_int_value(
        Mapper sut,
        IntBag source
    ) {
        BoxedIntBag actual = sut.map(
            source,
            IntBag.class,
            BoxedIntBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_int_value_to_int_value(
        Mapper sut,
        BoxedIntBag source
    ) {
        IntBag actual = sut.map(
            source,
            BoxedIntBag.class,
            IntBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class LongBag {
        private final long value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedLongBag {
        private final Long value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_long_value(Mapper sut, LongBag source) {
        LongBag actual = sut.map(source, LongBag.class, LongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_long_value(
        Mapper sut,
        BoxedLongBag source
    ) {
        BoxedLongBag actual = sut.map(
            source,
            BoxedLongBag.class,
            BoxedLongBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_long_value_to_boxed_long_value(
        Mapper sut,
        LongBag source
    ) {
        BoxedLongBag actual = sut.map(
            source,
            LongBag.class,
            BoxedLongBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_long_value_to_long_value(
        Mapper sut,
        BoxedLongBag source
    ) {
        LongBag actual = sut.map(
            source,
            BoxedLongBag.class,
            LongBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class FloatBag {
        private final float value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedFloatBag {
        private final Float value;
    }

    @AutoParameterizedTest
    void suit_correctly_converts_float_value(Mapper sut, FloatBag source) {
        FloatBag actual = sut.map(source, FloatBag.class, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_float_value(
        Mapper sut,
        BoxedFloatBag source
    ) {
        BoxedFloatBag actual = sut.map(
            source,
            BoxedFloatBag.class,
            BoxedFloatBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_float_value_to_boxed_float_value(
        Mapper sut,
        FloatBag source
    ) {
        BoxedFloatBag actual = sut.map(
            source,
            FloatBag.class,
            BoxedFloatBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_float_value_to_float_value(
        Mapper sut,
        BoxedFloatBag source
    ) {
        FloatBag actual = sut.map(
            source,
            BoxedFloatBag.class,
            FloatBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class DoubleBag {
        private final double value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedDoubleBag {
        private final Double value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_double_value(Mapper sut, DoubleBag source) {
        DoubleBag actual = sut.map(source, DoubleBag.class, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_double_value(
        Mapper sut,
        BoxedDoubleBag source
    ) {
        BoxedDoubleBag actual = sut.map(
            source,
            BoxedDoubleBag.class,
            BoxedDoubleBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_double_value_to_boxed_double_value(
        Mapper sut,
        DoubleBag source
    ) {
        BoxedDoubleBag actual = sut.map(
            source,
            DoubleBag.class,
            BoxedDoubleBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_double_value_to_double_value(
        Mapper sut,
        BoxedDoubleBag source
    ) {
        DoubleBag actual = sut.map(
            source,
            BoxedDoubleBag.class,
            DoubleBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class CharBag {
        private final char value;
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedCharBag {
        private final Character value;
    }

    @AutoParameterizedTest
    void sut_correctly_converts_char_value(Mapper sut, CharBag source) {
        CharBag actual = sut.map(source, CharBag.class, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_char_value(
        Mapper sut,
        BoxedCharBag source
    ) {
        BoxedCharBag actual = sut.map(
            source,
            BoxedCharBag.class,
            BoxedCharBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_char_value_to_boxed_char_value(
        Mapper sut,
        CharBag source
    ) {
        BoxedCharBag actual = sut.map(
            source,
            CharBag.class,
            BoxedCharBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AutoParameterizedTest
    void sut_correctly_converts_boxed_char_value_to_char_value(
        Mapper sut,
        BoxedCharBag source
    ) {
        CharBag actual = sut.map(
            source,
            BoxedCharBag.class,
            CharBag.class);

        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }
}
