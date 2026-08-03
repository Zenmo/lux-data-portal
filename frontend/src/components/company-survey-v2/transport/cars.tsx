import {UseFormReturn} from 'react-hook-form'
import {NumberRow} from '../generic/number-row'
import {ProjectName} from '../project'
import {DriveSchedules} from './drive-schedules'

export const Cars = ({form, prefix, project}: { form: UseFormReturn, prefix: string, project: ProjectName}) => {
    const {register, watch} = form

    const numCars = watch(`${prefix}.numCars`)

    return (
        <>
            <h3>Bedrijfsauto's</h3>
            <NumberRow
                label="Aantal bedrijfsauto's (die niet mee naar huis gaan)?"
                name={`${prefix}.numCars`}
                form={form} />
            {numCars > 0 && (
                <>
                    <NumberRow
                        label="Hoeveel van die bedrijfsauto's zijn elektrisch?"
                        name={`${prefix}.numElectricCars`}
                        form={form} />
                    <NumberRow
                        label={<span>Hoeveel <b>laadpunten</b> voor elektrische bedrijfsauto's zijn er?</span>}
                        name={`${prefix}.numChargePoints`}
                        form={form} />
                    <NumberRow
                        label="Wat is het maximale laadvermogen per laadpunt?"
                        name={`${prefix}.powerPerChargePointKw`}
                        suffix="kW"
                        form={form} />
                    <NumberRow
                        label="Hoeveel rijden uw bedrijfsauto's gemiddeld per jaar (grove inschatting)?"
                        name={`${prefix}.annualTravelDistancePerCarKm`}
                        suffix="km"
                        form={form} />
                    <NumberRow
                        label="Hoeveel van de brandstof bedrijfsauto's bent u van plan te elektrificeren de komende 5 jaar?"
                        name={`${prefix}.numPlannedElectricCars`}
                        form={form} />
                    {project === 'De Wieken' && (
                        <NumberRow
                            label="Hoeveel van de brandstof bedrijfsauto's bent u van plan aan te drijven met waterstof de komende 5 jaar?"
                            name={`${prefix}.numPlannedHydrogenCars`}
                            form={form} />
                    )}
                    <DriveSchedules form={form} prefix={prefix} totalVehicles={numCars} />
                </>
            )}
        </>
    )
}