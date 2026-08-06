import {UseFormReturn} from 'react-hook-form'
import {FormRow} from './generic/form-row'
import {NumberRow} from './generic/number-row'
import {TextInput} from './generic/text-input'

export const Address = ({form, prefix}: { form: UseFormReturn , prefix: string }) => {
    return (
        <>
            <FormRow
                label="Straat"
                name={`${prefix}.street`}
                form={form}
                InputComponent={TextInput}
                options={{required: true}} />
            <NumberRow
                label="Huisnummer"
                name={`${prefix}.houseNumber`}
                form={form}
                options={{required: true, min: 1}} />
            <FormRow
                label="Huisletter"
                name={`${prefix}.houseLetter`}
                form={form}
                InputComponent={TextInput}
                options={{maxLength: 1, pattern: /^[a-z]$/i}} />
            <FormRow
                label="Toevoeging"
                name={`${prefix}.houseNumberSuffix`}
                form={form}
                InputComponent={TextInput} />
            <FormRow
                label="Postcode"
                name={`${prefix}.postalCode`}
                form={form}
                InputComponent={TextInput}
                options={{required: true}} />
            <FormRow
                label="Plaats"
                name={`${prefix}.city`}
                form={form}
                InputComponent={TextInput}
                options={{required: true}}/>
        </>
    )
}