import {css} from '@emotion/react'
import React from 'react'
import {AppHook} from '../services/appState'
import {Verblijfsobject} from '../services/bag-verblijfsobject'
import {Bag2DPand} from '../services/bag2d'
import {count, empty, filter, flatMap, map, reduce, toIterable} from '../services/iterable'

const statItemStyle = css({
    borderTop: "1px solid #e5e7eb",
    padding: "0.875rem 0",
})

const statLabelStyle = css({
    fontSize: "0.7rem",
    color: "#9ca3af",
    margin: 0,
    marginBottom: "0.15rem",
})

const statValueStyle = css({
    fontSize: "1.35rem",
    fontWeight: 700,
    color: "#1e3a5f",
    margin: 0,
    lineHeight: 1.1,
})

const gebruiksdoelItemStyle = css({
    display: "flex",
    alignItems: "center",
    gap: "0.5rem",
    fontSize: "0.875rem",
    color: "#374151",
    padding: "0.25rem 0",
})

const dotStyle = css({
    width: "6px",
    height: "6px",
    borderRadius: "50%",
    backgroundColor: "#1e3a5f",
    flexShrink: 0,
})

export const AggregatedAreaData = ({appHook: {bag2dPanden, verblijfsobjecten}}: { appHook: AppHook }) => {
    const pandCount = count(bag2dPanden)
    const bouwjaar = averageBouwjaar(bag2dPanden)
    const verblijfsobjectCount = count(verblijfsobjecten)
    const vloeroppervlak = sumVloeroppervlak(verblijfsobjecten).toLocaleString('nl-NL')
    const gebruiksdoelen = [...gebruiksdoelenOverzicht(verblijfsobjecten).entries()]

    return (
        <div>
            <div css={statItemStyle}>
                <p css={statLabelStyle}>Aantal panden</p>
                <p css={statValueStyle}>{pandCount.toLocaleString('nl-NL')}</p>
            </div>
            <div css={statItemStyle}>
                <p css={statLabelStyle}>Gemiddeld bouwjaar</p>
                <p css={statValueStyle}>{bouwjaar}</p>
            </div>
            <div css={statItemStyle}>
                <p css={statLabelStyle}>Aantal verblijfsobjecten</p>
                <p css={statValueStyle}>{verblijfsobjectCount.toLocaleString('nl-NL')}</p>
            </div>
            <div css={statItemStyle}>
                <p css={statLabelStyle}>Vloeroppervlak</p>
                <p css={statValueStyle}>
                    {vloeroppervlak} <span css={{fontSize: "1.25rem",}}>m²</span>
                </p>
            </div>
            {gebruiksdoelen.length > 0 && (
                <div css={statItemStyle}>
                    <p css={statLabelStyle}>Gebruiksdoelen</p>
                    {gebruiksdoelen.map(([gebruiksdoel, aantal]) => (
                        <div key={gebruiksdoel} css={gebruiksdoelItemStyle}>
                            <span css={dotStyle} />
                            <span>{gebruiksdoel} ({aantal}x)</span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}

const sumVloeroppervlak = (verblijfsobjecten: Iterable<Verblijfsobject>): number =>
    reduce(
        map(verblijfsobjecten, verblijfsobject => verblijfsobject.oppervlakte),
        (acc, val) => acc + val,
        0,
    )

const averageBouwjaar = (panden: Iterable<Bag2DPand>) => {
    const bouwjaren = toIterable(() => filter(
        map(panden, pand => pand.properties.bouwjaar),
        bouwjaar => Boolean(bouwjaar),
    ))

    if (empty(bouwjaren)) {
        return 1995
    }

    const sum = reduce(bouwjaren, (acc, val) => acc + val, 0)

    const average = sum / count(bouwjaren)

    return Math.round(average)
}

const gebruiksdoelenOverzicht = (verblijfsobjecten: Iterable<Verblijfsobject>): Map<string, number> =>
    reduce(
        flatMap(verblijfsobjecten, verblijfsobject => verblijfsobject.gebruiksdoelen),
        (acc, gebruiksdoel) => acc.set(gebruiksdoel, (acc.get(gebruiksdoel) ?? 0) + 1),
        new Map<string, number>(),
    )