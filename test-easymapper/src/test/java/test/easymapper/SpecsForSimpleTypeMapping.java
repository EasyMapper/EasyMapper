package test.easymapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import autoparams.Repeat;
import easymapper.Mapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecsForSimpleTypeMapping {

    @AllArgsConstructor
    @Getter
    public static class Post {

        private final UUID id;
        private final UUID authorId;
        private final String title;
        private final String text;
    }

    @Getter
    @Setter
    public static class PostView {

        private String id;
        private String authorId;
        private String title;
        private String text;
    }

    @Test
    @AutoDomainParams
    void convert_converts_UUID_to_String(Mapper sut, Post source) {
        PostView actual = sut.convert(source, PostView.class);

        assertThat(actual.getId()).isEqualTo(source.getId().toString());
        assertThat(actual.getAuthorId())
            .isEqualTo(source.getAuthorId().toString());
    }

    @Test
    @AutoDomainParams
    @UseNull(UUID.class)
    void convert_converts_null_UUID_to_null_String(Mapper sut, Post source) {
        PostView actual = sut.convert(source, PostView.class);
        assertThat(actual.getId()).isNull();
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_BigInteger_value(
        Mapper sut,
        BigInteger source
    ) {
        BigInteger actual = sut.convert(source, BigInteger.class);
        assertThat(actual).isEqualTo(source);
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_BigDecimal_value(
        Mapper sut,
        BigDecimal source
    ) {
        BigDecimal actual = sut.convert(source, BigDecimal.class);
        assertThat(actual).isEqualTo(source);
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_LocalDate_value(
        Mapper sut,
        LocalDate source
    ) {
        LocalDate actual = sut.convert(source, LocalDate.class);
        assertThat(actual).isEqualTo(source);
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_LocalTime_value(
        Mapper sut,
        LocalTime source
    ) {
        LocalTime actual = sut.convert(source, LocalTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_LocalDateTime_value(
        Mapper sut,
        LocalDateTime source
    ) {
        LocalDateTime actual = sut.convert(source, LocalDateTime.class);
        assertThat(actual).isEqualTo(source);
    }

    @AllArgsConstructor
    public static class BooleanBag {

        private final boolean value;

        public boolean getValue() {
            return value;
        }
    }

    @ParameterizedTest
    @AutoDomainSource
    @Repeat(10)
    void convert_correctly_converts_boolean_value(
        Mapper sut,
        BooleanBag source
    ) {
        BooleanBag actual = sut.convert(source, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Getter
    @AllArgsConstructor
    public static class BoxedBooleanBag {

        private final Boolean value;
    }

    @ParameterizedTest
    @AutoDomainSource
    @Repeat(10)
    void convert_correctly_converts_boxed_boolean_value(
        Mapper sut,
        BoxedBooleanBag source
    ) {
        BoxedBooleanBag actual = sut.convert(source, BoxedBooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @ParameterizedTest
    @AutoDomainSource
    @Repeat(10)
    void convert_correctly_converts_boolean_value_to_boxed_boolean_value(
        Mapper sut,
        BooleanBag source
    ) {
        BoxedBooleanBag actual = sut.convert(source, BoxedBooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @ParameterizedTest
    @AutoDomainSource
    @Repeat(10)
    void convert_correctly_converts_boxed_boolean_value_to_boolean_value(
        Mapper sut,
        BoxedBooleanBag source
    ) {
        BooleanBag actual = sut.convert(source, BooleanBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ByteBag {

        private final byte value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_byte_value(Mapper sut, ByteBag source) {
        ByteBag actual = sut.convert(source, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedByteBag {

        private final Byte value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_byte_value(
        Mapper sut,
        BoxedByteBag source
    ) {
        BoxedByteBag actual = sut.convert(source, BoxedByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_byte_value_to_boxed_byte_value(
        Mapper sut,
        ByteBag source
    ) {
        BoxedByteBag actual = sut.convert(source, BoxedByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_byte_value_to_byte_value(
        Mapper sut,
        BoxedByteBag source
    ) {
        ByteBag actual = sut.convert(source, ByteBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class ShortBag {

        private final short value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_short_value(Mapper sut, ShortBag source) {
        ShortBag actual = sut.convert(source, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedShortBag {

        private final Short value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_short_value(
        Mapper sut,
        BoxedShortBag source
    ) {
        BoxedShortBag actual = sut.convert(source, BoxedShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_short_value_to_boxed_short_value(
        Mapper sut,
        ShortBag source
    ) {
        BoxedShortBag actual = sut.convert(source, BoxedShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_short_value_to_short_value(
        Mapper sut,
        BoxedShortBag source
    ) {
        ShortBag actual = sut.convert(source, ShortBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class IntBag {

        private final int value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_int_value(Mapper sut, IntBag source) {
        IntBag actual = sut.convert(source, IntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedIntBag {

        private final Integer value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_int_value(
        Mapper sut,
        BoxedIntBag source
    ) {
        BoxedIntBag actual = sut.convert(source, BoxedIntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_int_value_to_boxed_int_value(
        Mapper sut,
        IntBag source
    ) {
        BoxedIntBag actual = sut.convert(source, BoxedIntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_int_value_to_int_value(
        Mapper sut,
        BoxedIntBag source
    ) {
        IntBag actual = sut.convert(source, IntBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class LongBag {

        private final long value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_long_value(Mapper sut, LongBag source) {
        LongBag actual = sut.convert(source, LongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedLongBag {

        private final Long value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_long_value(
        Mapper sut,
        BoxedLongBag source
    ) {
        BoxedLongBag actual = sut.convert(source, BoxedLongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_long_value_to_boxed_long_value(
        Mapper sut,
        LongBag source
    ) {
        BoxedLongBag actual = sut.convert(source, BoxedLongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_long_value_to_long_value(
        Mapper sut,
        BoxedLongBag source
    ) {
        LongBag actual = sut.convert(source, LongBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class FloatBag {

        private final float value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_float_value(Mapper sut, FloatBag source) {
        FloatBag actual = sut.convert(source, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedFloatBag {

        private final Float value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_float_value(
        Mapper sut,
        BoxedFloatBag source
    ) {
        BoxedFloatBag actual = sut.convert(source, BoxedFloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_float_value_to_boxed_float_value(
        Mapper sut,
        FloatBag source
    ) {
        BoxedFloatBag actual = sut.convert(source, BoxedFloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_float_value_to_float_value(
        Mapper sut,
        BoxedFloatBag source
    ) {
        FloatBag actual = sut.convert(source, FloatBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class DoubleBag {

        private final double value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_double_value(Mapper sut, DoubleBag source) {
        DoubleBag actual = sut.convert(source, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedDoubleBag {

        private final Double value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_double_value(
        Mapper sut,
        BoxedDoubleBag source
    ) {
        BoxedDoubleBag actual = sut.convert(source, BoxedDoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_double_value_to_boxed_double_value(
        Mapper sut,
        DoubleBag source
    ) {
        BoxedDoubleBag actual = sut.convert(source, BoxedDoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_double_value_to_double_value(
        Mapper sut,
        BoxedDoubleBag source
    ) {
        DoubleBag actual = sut.convert(source, DoubleBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class CharBag {

        private final char value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_char_value(Mapper sut, CharBag source) {
        CharBag actual = sut.convert(source, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @AllArgsConstructor
    @Getter
    public static class BoxedCharBag {

        private final Character value;
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_char_value(
        Mapper sut,
        BoxedCharBag source
    ) {
        BoxedCharBag actual = sut.convert(source, BoxedCharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_char_value_to_boxed_char_value(
        Mapper sut,
        CharBag source
    ) {
        BoxedCharBag actual = sut.convert(source, BoxedCharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }

    @Test
    @AutoDomainParams
    void convert_correctly_converts_boxed_char_value_to_char_value(
        Mapper sut,
        BoxedCharBag source
    ) {
        CharBag actual = sut.convert(source, CharBag.class);
        assertThat(actual.getValue()).isEqualTo(source.getValue());
    }
}
