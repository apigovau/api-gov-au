# Matching client information to ATO records

We match taxpayers using their surname and date of birth.

These values must be the same as stored in ATO systems or the lodgment will be rejected and error message returned to the user.  

Changes of demographic, address and contact information on amendments are not advised as this will cause processing of the amendment to cease. 

ATO systems will not inform the user during processing when the amendment data differs from client records but will indicate via SBR that the amendment has been accepted successfully.

The demographic, contact and address information will then be validated manually by an ATO officer (including contacting the tax agent) before the processing of the amendment can continue.

Consider if you should let your users modify any of these fields:

| Report Label | SBR Alias | ELS tag  | 
| --- | --- | --- |
| Year of return | IITR10 | ABB | 
| Tax file number | IITR15 | AAD | 
| Title | IITR21 | ABE | 
| Family name | IITR22 | ABF | 
| Suffix | IITR23 | BAW | 
| First name | IITR24 | ABG | 
| Other given names | IITR25 | BBB | 
| Has name or title changed since last tax return lodged | IITR26 | BFG | 
| Current postal address - Address line 1 | IITR33 | ABH | 
| Current postal address - Address line 2 | IITR34 |  | 
| Current postal address - Suburb/Town | IITR35 | AME | 
| Current postal address - State | IITR36 | AMF | 
| Current postal address - Postcode | IITR37 | APE | 
| Current postal address - Country code | IITR38 | KGD | 
| Postal address changed | IITR39 | BFH | 
| Home address - Address line 1 | IITR41 | ABK | 
| Home address - Address line 2 | IITR42 |  | 
| Home address - Suburb/Town | IITR43 | AXQ | 
| Home address - State | IITR44 | AXR | 
| Home address - Postcode | IITR45 | APH | 
| Home address - Country code | IITR52 | KGE | 
| Date of birth | IITR29 | ABQ | 
| Date of death | IITR28 | ARH | 
| Your mobile phone number | IITR48 | KGR | 
| Daytime phone area code | IITR49 | BOC | 
| Daytime phone number | IITR50 | BOD | 
| Contact E-mail address | IITR51 | FLW |  