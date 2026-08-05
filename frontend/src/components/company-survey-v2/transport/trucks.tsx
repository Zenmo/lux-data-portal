import {UseFormReturn} from 'react-hook-form'
import {NumberRow} from '../generic/number-row'
import {ProjectName} from '../project'
import {DriveSchedules} from './drive-schedules'

export const Trucks = ({form, prefix, project}: { form: UseFormReturn, prefix: string, project: ProjectName}) => {
    const {register, watch} = form

    const numTrucks = watch(`${prefix}.numTrucks`)
    const numElectricTrucks = watch(`${prefix}.numElectricTrucks`)

    return (
        <>
            <h3>Vrachtwagens/trucks</h3>
            <NumberRow
                label="Aantal vrachtwagens/trucks die 's nachts op deze locatie gestationeerd zijn?"
                name={`${prefix}.numTrucks`}
                form={form} />
            {numTrucks > 0 && (
                <>
                    <NumberRow
                        label="Hoeveel rijden uw trucks gemiddeld per jaars (ongeveer)?"
                        name={`${prefix}.annualTravelDistancePerTruckKm`}
                        form={form}
                        suffix="km" />                
                    <NumberRow
                        label="Hoeveel van uw trucks zijn elektrisch?"
                        name={`${prefix}.numElectricTrucks`}
                        form={form} />
                    {numElectricTrucks > 0 && (
                        <>
                            <NumberRow
                                label="Hoeveel laadpunten voor elektrische trucks zijn er?"
                                name={`${prefix}.numChargePoints`}
                                form={form} />
                            <NumberRow
                                label="Wat is het maximale laadvermogen van per laadpunt?"
                                name={`${prefix}.powerPerChargePointKw`}
                                form={form}
                                suffix="kW" />
                         </>  
                     )}        
                    <NumberRow
                        label="Hoeveel (niet-elektrische) trucks bent u van plan te elektrificeren de komende 5 jaar?"
                        name={`${prefix}.numPlannedElectricTrucks`}
                        form={form} />
                    {project === 'De Wieken' && (
                        <NumberRow
                            label="Hoeveel van de brandstof vrachtwagens bent u van plan aan te drijven met waterstof de komende 5 jaar?"
                            name={`${prefix}.numPlannedHydrogenTrucks`}
                            form={form} />
                    )}
                    <DriveSchedules form={form} prefix={prefix} totalVehicles={numTrucks} />
                </>
            )}
        </>
    )
}