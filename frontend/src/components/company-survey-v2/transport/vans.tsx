import {UseFormReturn} from 'react-hook-form'
import {NumberRow} from '../generic/number-row'
import {ProjectName} from '../project'
import {DriveSchedules} from './drive-schedules'

export const Vans = ({form, prefix, project}: { form: UseFormReturn, prefix: string, project: ProjectName}) => {
    const {register, watch} = form

    const numVans = watch(`${prefix}.numVans`)
    const numElectricVans = watch(`${prefix}.numElectricVans`)

    return (
        <>
            <h3>Busjes</h3>
            <NumberRow
                label="Aantal (bestel)busjes (die niet mee naar huis gaan)?"
                name={`${prefix}.numVans`}
                form={form} />
            {numVans > 0 && (
                <>
                    <NumberRow
                        label="Hoeveel rijden uw busjes gemiddeld per jaar (grove inschatting)?"
                        name={`${prefix}.annualTravelDistancePerVanKm`}
                        form={form}
                        suffix="km" />
                    <NumberRow
                        label="Hoeveel van die bestelbusjes zijn elektrisch?"
                        name={`${prefix}.numElectricVans`}
                        form={form} />
                    {numVans > 0 && (
                        <>
                        <NumberRow
                            label={<span>Hoeveel <b>laadpunten</b> voor elektrische busjes zijn er?</span>}
                            name={`${prefix}.numChargePoints`}
                            form={form} />
                        <NumberRow
                            label="Wat is het maximale laadvermogen per laadpunt?"
                            name={`${prefix}.powerPerChargePointKw`}
                            form={form}
                            suffix="kW" />
                        </>
                    )}
                    <NumberRow
                        label="Hoeveel van de brandstof busjes bent u van plan te elektrificeren de komende 5 jaar?"
                        name={`${prefix}.numPlannedElectricVans`}
                        form={form} />
                    {project === 'De Wieken' && (
                        <NumberRow
                            label="Hoeveel van de brandstof busjes bent u van plan aan te drijven met waterstof de komende 5 jaar?"
                            name={`${prefix}.numPlannedHydrogenVans`}
                            form={form} />
                    )}
                    <DriveSchedules form={form} prefix={prefix} totalVehicles={numVans} />
                </>
            )}
        </>
    )
}