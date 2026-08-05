import {css} from "@emotion/react"
import {get} from "lodash"
import {Message} from "primereact/message"
import {useFieldArray, UseFormReturn} from "react-hook-form"
import {NumberRow} from "../generic/number-row"
import {BsPlus, BsTrash} from "react-icons/bs"

const dayOptions = [
    {value: "MONDAY", label: "Maandag"},
    {value: "TUESDAY", label: "Dinsdag"},
    {value: "WEDNESDAY", label: "Woensdag"},
    {value: "THURSDAY", label: "Donderdag"},
    {value: "FRIDAY", label: "Vrijdag"},
    {value: "SATURDAY", label: "Zaterdag"},
    {value: "SUNDAY", label: "Zondag"},
]

type TripFormValues = {
    dayOfWeek: string
    startTime: string
    endTime: string
}

type DriveScheduleFormValues = {
    nVehicles: number
    trips: TripFormValues[]
}

function validateEndTime(
    endTime: string,
    startTime: string,
    allTrips: TripFormValues[],
    dayOfWeek: string,
    selfIndex: number,
): string | true {
    if (endTime <= startTime) return "Aankomsttijd moet na vertrektijd liggen"

    const overlaps = allTrips.some((other, j) =>
        j !== selfIndex &&
        other.dayOfWeek === dayOfWeek &&
        other.startTime && other.endTime &&
        startTime < other.endTime && endTime > other.startTime,
    )
    return overlaps ? "Rit overlapt met een andere rit op dezelfde dag" : true
}

function validateVehicleCount(
    allSchedules: DriveScheduleFormValues[],
    totalVehicles: number,
): string | true {
    const total = allSchedules.reduce((sum, s) => sum + s.nVehicles, 0)
    return total <= totalVehicles
        || `Totaal (${total}) overschrijdt aantal voertuigen (${totalVehicles})`
}

const thStyle = css`
    text-align: left;
    padding-right: 8px;
    padding-bottom: 4px;
    font-weight: 500;
    font-size: 13px;
    color: rgba(0, 0, 0, 0.65);
`

const tdStyle = css`
    padding-right: 8px;
    padding-bottom: 6px;
    vertical-align: top;
`

const TripRow = ({form, prefix, schedulePrefix, tripIndex, onRemove}: {
    form: UseFormReturn,
    prefix: string,
    schedulePrefix: string,
    tripIndex: number,
    onRemove: () => void,
}) => {
    const {register, getValues, formState: {errors}} = form
    const endTimeError = get(errors, `${prefix}.endTime`)

    return (
        <tr>
            <td css={tdStyle}>
                <select {...register(`${prefix}.dayOfWeek`)}>
                    {dayOptions.map(opt => (
                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                </select>
            </td>
            <td css={tdStyle}>
                <input
                    type="time"
                    {...register(`${prefix}.startTime`, {required: true})}
                />
            </td>
            <td css={tdStyle}>
                <input
                    type="time"
                    css={endTimeError && css`border-color: red;`}
                    {...register(`${prefix}.endTime`, {
                        required: true,
                        validate: value => {
                            const startTime = getValues(`${prefix}.startTime`) as string
                            if (!startTime || !value) return true
                            return validateEndTime(
                                value,
                                startTime,
                                getValues(`${schedulePrefix}.trips`) as TripFormValues[],
                                getValues(`${prefix}.dayOfWeek`) as string,
                                tripIndex,
                            )
                        },
                    })}
                />
                {endTimeError && (
                    <div css={{color: "red", fontSize: 12, marginTop: 2}}>
                        {String(endTimeError.message)}
                    </div>
                )}
            </td>
            <td css={tdStyle}>
                <button
                    type="button"
                    css={{border: "none", backgroundColor: "transparent", color: "red"}}
                    onClick={onRemove}
                ><BsTrash />
                </button>
            </td>
        </tr>
    )
}

const DriveScheduleCard = ({form, prefix, schedulesPrefix, index, totalVehicles, onRemove}: {
    form: UseFormReturn,
    prefix: string,
    schedulesPrefix: string,
    index: number,
    totalVehicles: number,
    onRemove: () => void,
}) => {
    const {control} = form
    const {fields: trips, append: appendTrip, remove: removeTrip} = useFieldArray({
        control,
        name: `${prefix}.trips`,
    })

    return (
        <div css={{border: "1px solid #d9d9d9", borderRadius: 4, marginBottom: 12}}>
            <div css={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                padding: "8px 12px",
                borderBottom: "1px solid #d9d9d9",
                background: "#fafafa",
            }}>
                <h6>Weekplanning {index + 1}</h6>
                <button
                    type="button"
                    css={{border: "none", backgroundColor: "transparent", color: "red"}}
                    onClick={onRemove}
                >
                    <BsTrash /> Verwijderen
                </button>
            </div>
            <div css={{padding: 12}}>
                <NumberRow
                    label="Aantal voertuigen in dit schema"
                    name={`${prefix}.nVehicles`}
                    form={form}
                    options={{
                        min: 1,
                        required: true,
                        validate: () => validateVehicleCount(
                            form.getValues(schedulesPrefix) as DriveScheduleFormValues[],
                            totalVehicles,
                        ),
                    }}
                />
                {trips.length === 0 ? (
                    <div css={{display: "flex", justifyContent: "center", margin: "8px 0"}}>
                        <Message severity="warn" text="Voeg minimaal één rit toe aan dit schema" />
                    </div>
                ) : (
                    <table css={{borderCollapse: "collapse", marginTop: 8, marginBottom: 8, marginLeft: "auto", marginRight: "auto"}}>
                        <thead>
                        <tr>
                            <th css={thStyle}>Dag</th>
                            <th css={thStyle}>Vertrektijd</th>
                            <th css={thStyle}>Aankomsttijd</th>
                            <th />
                        </tr>
                        </thead>
                        <tbody>
                        {trips.map((trip, i) => (
                            <TripRow
                                key={trip.id}
                                form={form}
                                prefix={`${prefix}.trips.${i}`}
                                schedulePrefix={prefix}
                                tripIndex={i}
                                onRemove={() => removeTrip(i)}
                            />
                        ))}
                        </tbody>
                    </table>
                )}
                <div css={{display: "flex", justifyContent: "center"}}>
                    <button
                        type="button"
                        onClick={() => appendTrip({dayOfWeek: "MONDAY", startTime: "08:00", endTime: "17:00"})}
                    ><BsPlus /> Rit toevoegen
                    </button>
                </div>
            </div>
        </div>
    )
}

export const DriveSchedules = ({form, prefix, totalVehicles}: {
    form: UseFormReturn,
    prefix: string,
    totalVehicles: number,
}) => {
    const {control} = form
    const schedulesPrefix = `${prefix}.driveSchedules`
    const {fields: schedules, append, remove} = useFieldArray({
        control,
        name: schedulesPrefix,
        keyName: "fieldId",
    })

    return (
        <div css={{marginTop: 16}}>
            <h4>Rittenplanning</h4>
            <p css={{marginBottom: 12}}>
                Elektrisch laden heeft een grote impact op de netbelasting. Het biedt ook kansen voor het
                efficiënt benutten van de beschikbare aansluitcapaciteit en de eigen opwek. Voor een
                gedetailleerde analyse vul je hier in wanneer de voertuigen niet op locatie zijn.
            </p>
            {schedules.map((schedule, i) => (
                <DriveScheduleCard
                    key={schedule.fieldId}
                    form={form}
                    prefix={`${schedulesPrefix}.${i}`}
                    schedulesPrefix={schedulesPrefix}
                    index={i}
                    totalVehicles={totalVehicles}
                    onRemove={() => remove(i)}
                />
            ))}
            <button
                type="button"
                onClick={() => append({id: crypto.randomUUID(), nVehicles: 1, trips: []})}
            ><BsPlus /> Weekplanning toevoegen
            </button>
        </div>
    )
}